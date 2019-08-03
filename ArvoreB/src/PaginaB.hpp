/**
 * @file PaginaB.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo da classe PaginaB.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#include "templates/tipos.hpp"
#include "templates/serializavel.hpp"
#include "helpersArvore.hpp"

#include <iostream>
#include <algorithm>

using namespace std;

namespace constantes
{
    static const file_ptr_type ptrNuloPagina = -1;
}

/**
 * @brief Classe com as características da página da árvore B.
 * 
 * <p>Caso for usar o método mostrar() dá classe, é necessário que a chave e o dado
 * sejam tipos primitivos ou então o operador << deve ser sobrecarregado para que
 * seja possível inserir a chave e/ou o dado num ostream (output stream).</p>
 * 
 * @see [Sobrecarregando o operador <<](https://docs.microsoft.com/pt-br/cpp/standard-library/overloading-the-output-operator-for-your-own-classes?view=vs-2019)
 * 
 * @tparam TIPO_DAS_CHAVES Tipo da chave dos registros. <b>É necessário que a chave
 * seja um tipo primitivo ou então que a sua classe/struct herde de Serializavel e
 * tenha um construtor sem parâmetros.</b>
 * @tparam TIPO_DOS_DADOS Tipo do dado dos registros. <b>É necessário que o dado
 * seja um tipo primitivo ou então que a sua classe/struct herde de Serializavel e
 * tenha um construtor sem parâmetros.</b>
 */
template <typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
class PaginaB : public Serializavel
{
protected:
    // ------------------------- Campos

    int _tamanho;
    int maximoDeBytesParaAChave;
    int maximoDeBytesParaODado;
    int numeroDeChavesPorPagina;
    int ordemDaArvore;
    file_ptr_type endereco;

public:
    // ------------------------- Typedefs

    /**
     * @brief Padroniza o tipo da página da árvore. Typedefs dentro de classes ou
     * structs são considerados como boa prática em C++.
     */
    typedef PaginaB<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> Pagina;

    // ------------------------- Campos

    vector<TIPO_DAS_CHAVES> chaves;
    vector<TIPO_DOS_DADOS> dados;
    vector<file_ptr_type> ponteiros;

    // ------------------------- Construtores

    PaginaB() : _tamanho(0), endereco(constantes::ptrNuloPagina) {}

    /**
     * @brief Constrói uma nova página com a ordem informada.
     * 
     * @param ordemDaArvore Ordem da árvore B (quantidade máxima de filhos por página).
     * @param maximoDeBytesParaAChave Quantidade máxima de bytes que a chave pode gastar
     * nos registros.
     * @param maximoDeBytesParaODado Quantidade máxima de bytes que o dado pode gastar
     * nos registros.
     */
    PaginaB(int ordemDaArvore,
            int maximoDeBytesParaAChave,
            int maximoDeBytesParaODado) :

            _tamanho(0),
            maximoDeBytesParaAChave(maximoDeBytesParaAChave),
            maximoDeBytesParaODado(maximoDeBytesParaODado),
            numeroDeChavesPorPagina(ordemDaArvore - 1),
            ordemDaArvore(ordemDaArvore),
            endereco(constantes::ptrNuloPagina)
    {
        chaves.reserve(numeroDeChavesPorPagina);
        dados.reserve(numeroDeChavesPorPagina);
        ponteiros.reserve(ordemDaArvore);

        ponteiros.push_back(constantes::ptrNuloPagina);
    }

    /**
     * @brief Constrói uma nova página com a ordem informada e infere automaticamente
     * a quantidade de bytes que o TIPO_DAS_CHAVES e o TIPO_DOS_DADOS gasta.
     * 
     * @param ordemDaArvore Ordem da árvore B (quantidade máxima de filhos por página).
     */
    PaginaB(int ordemDaArvore) : PaginaB(ordemDaArvore, 0, 0)
    {
        obterTamanhoEmBytesDaChaveEDoDado<TIPO_DAS_CHAVES, TIPO_DOS_DADOS>(
            maximoDeBytesParaAChave, maximoDeBytesParaODado
        );
    }

    /**
     * @brief Constrói uma nova página a partir do vetor de bytes do DataInputStream.
     * 
     * @param bytes DataInputStream com o vetor de bytes da página.
     * @param ordemDaArvore Ordem da árvore B (quantidade máxima de filhos por página).
     * @param maximoDeBytesParaAChave Quantidade máxima de bytes que a chave pode gastar
     * nos registros.
     * @param maximoDeBytesParaODado Quantidade máxima de bytes que o dado pode gastar
     * nos registros.
     */
    PaginaB(DataInputStream &bytes, int ordemDaArvore) : PaginaB(ordemDaArvore)
    {
        lerBytes(bytes);
    }

    // ------------------------- Métodos herdados de Serializavel

    virtual int obterTamanhoMaximoEmBytes() override
    {
        // bytes para guardar a quantidade de elementos na página
        return sizeof(decltype(_tamanho)) + // falta link aqui
               ordemDaArvore * sizeof(file_ptr_type) + // bytes para os ponteiros
               numeroDeChavesPorPagina * maximoDeBytesParaAChave + // bytes para as chaves
               numeroDeChavesPorPagina * maximoDeBytesParaODado; // bytes para os dados
    }

    virtual DataOutputStream &gerarDataOutputStream(DataOutputStream &out) override
    {
        TIPO_DAS_CHAVES *chave;
        TIPO_DOS_DADOS *dado;
        file_ptr_type ponteiro = ponteiros[0];

        out << _tamanho << ponteiro;

        for (size_t i = 0; i < _tamanho; i++)
        {
            chave = &chaves[i];
            dado = &dados[i];
            ponteiro = ponteiros[i + 1];

            out << chave;
            out << dado;
            out << ponteiro;
        }

        return out;
    }

    virtual void lerBytes(DataInputStream &input) override
    {
        // É necessário que a chave e o dado sejam tipos primitivos ou que
        // herdem a classe Serializavel e tenham um construtor sem parâmetros.
        TIPO_DAS_CHAVES chave;
        TIPO_DOS_DADOS dado;
        file_ptr_type ponteiro;

        input >> _tamanho >> ponteiro;
        ponteiros.push_back(ponteiro);

        for (size_t i = 0; i < _tamanho; i++)
        {
            input >> chave;
            input >> dado;
            input >> ponteiro; // é necessário pular todo o dado e toda a chave para que o ponteiro seja lido corretamente

            chaves.push_back(chave);
            dados.push_back(dado);
            ponteiros.push_back(ponteiro);
        }
    }

    // ------------------------- Métodos

    file_ptr_type setEndereco(file_ptr_type endereco)
    {
        if (endereco > (file_ptr_type) -1)
        {
            this->endereco = endereco;
        }
    }

    /**
     * @brief Retorna 0 (zero) caso a página não tenha sido carregada do arquivo ainda.
     * Caso contrário, retorna o seu número de elementos.
     * 
     * @return int O número de elementos da página.
     */
    int tamanho()
    {
        return _tamanho;
    }

    /**
     * @brief Obtém o endereço da página no arquivo.
     * 
     * @return file_ptr_type Endereço da página no arquivo.
     */
    file_ptr_type obterEndereco()
    {
        return endereco;
    }

    /**
     * @brief Checa se a página está sem elementos.
     * 
     * @return true Caso não haja elementos na página.
     * @return false Caso haja elementos na página.
     */
    bool vazia()
    {
        return _tamanho == 0;
    }

    /**
     * @brief Checa se a página está cheia.
     * 
     * @return true Caso a página esteja cheia.
     * @return false Caso a página não esteja cheia.
     */
    bool cheia()
    {
        return _tamanho == numeroDeChavesPorPagina;
    }

    /**
     * @brief Checa se esta página é uma folha.
     * 
     * @return true Caso a página esteja vazia ou o primeiro ponteiro dela seja nulo.
     * @return false Caso a página tenha algum elemento e o primeiro ponteiro não
     * seja nulo.
     */
    bool eUmaFolha()
    {
        return vazia() || ponteiros[0] == constantes::ptrNuloPagina;
    }

    /**
     * @brief Zera a quantidade de elementos e limpa todos os vetores internos.
     */
    void limpar()
    {
        _tamanho = 0;
        endereco = constantes::ptrNuloPagina;

        chaves.clear();
        dados.clear();
        ponteiros.clear();
    }

    /**
     * @brief Calcula qual é o índice em que a chave deve ser inserida ou então
     * qual é o índice do ponteiro para descer na árvore.
     * 
     * @param chave Chave de pesquisa.
     * 
     * @return int Índice em que a chave deve ser inserida ou então
     * o índice do ponteiro para descer na árvore.
     */
    int obterIndiceDeDescida(TIPO_DAS_CHAVES chave)
    {
        // lower_bound (pesquisa binária) -> http://www.cplusplus.com/reference/algorithm/lower_bound/
        auto iteradorDeInsercao =
            lower_bound(chaves.begin(), chaves.end(), chave);

        return iteradorDeInsercao - chaves.begin();
    }

    /**
     * @brief Insere a tripla (chave, dado, ponteiro) na página no índice informado.
     * 
     * @param chave Chave do par.
     * @param dado Dado do par.
     * @param indiceDeInsercao Índice de inserção do par (chave, dado).
     * @param ponteiro Ponteiro à direita do par (chave, dado).
     * @param inserirPonteiroADireita Indica se o ponteiro à direita do par
     * realmente deve ser criado.
     * 
     * @return true Caso tudo corra bem.
     * @return false Caso a página esteja cheia.
     */
    bool inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado, int indiceDeInsercao,
                 file_ptr_type ponteiro = constantes::ptrNuloPagina,
                 bool inserirPonteiroADireita = true)
    {
        bool sucesso = !cheia(); // Só é possível inserir se a página não estiver cheia

        if (sucesso)
        {
            chaves.insert(chaves.begin() + indiceDeInsercao, chave);
            dados.insert(dados.begin() + indiceDeInsercao, dado);

            if (inserirPonteiroADireita)
            {
                ponteiros.insert(ponteiros.begin() + indiceDeInsercao + 1, ponteiro);
            }
            
            _tamanho++;
        }

        return sucesso;
    }

    /**
     * @brief Insere o par (chave, dado) na página de forma ordenada.
     * 
     * @param chave Chave do par.
     * @param dado Dado do par.
     * 
     * @return int -1 caso a inserção falhe (página cheia). Caso contrário,
     * o índice onde o par foi inserido.
     */
    int inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado)
    {
        int indiceDeInsercao = obterIndiceDeDescida(chave);

        if (!inserir(chave, dado, indiceDeInsercao))
        {
            indiceDeInsercao = -1;
        }

        return indiceDeInsercao;
    }

    /**
     * @brief Pega o par (chave, dado) no indiceLocal e a insere no indiceNoDestino
     * da paginaDestino. Após isso, remove o par desta página.
     * 
     * @param paginaDestino Página destino.
     * @param indiceNoDestino Índice de inserção na página destino.
     * @param indiceLocal Índice do par (chave, dado) nesta página.
     * @param excluirPonteiroDaEsquerda Indica se o ponteiro à esquerda do par
     * deve ser excluído.
     * @param excluirPonteiroDaDireita Indica se o ponteiro à direita do par
     * deve ser excluído.
     * @param inserirPonteiroADireita Indica se o ponteiro à direita do par
     * realmente deve ser criado na paginaDestino.
     */
    void transferirElementoPara(
        Pagina *paginaDestino, int indiceNoDestino, int indiceLocal,
        bool excluirPonteiroDaEsquerda = false, bool excluirPonteiroDaDireita = false,
        bool inserirPonteiroADireita = true)
    {
        paginaDestino->inserir(
            chaves[indiceLocal], dados[indiceLocal],
            indiceNoDestino, constantes::ptrNuloPagina,
            inserirPonteiroADireita
        );

        excluir(indiceLocal, excluirPonteiroDaEsquerda, excluirPonteiroDaDireita);
    }

    /**
     * @brief Caso a inserção do par (chave, dado) tenha acontecido na nova página,
     * o primeiro elemento dela deve ser promovido (o mais à esquerda). Caso a
     * inserção tenha acontecido na página que estava cheia, o último elemento dela
     * deve ser promovido (o mais à direita).
     * 
     * @param paginaDestino Página destino.
     * @param indice Índice de inserção na página destino.
     * @param exPaginaCheia Ponteiro para a página que estava cheia e provocou a
     * divisão.
     * @param novaPagina Ponteiro para a nova página que foi criada na divisão.
     * @param promoverElementoDoFim Caso seja true, promove o último elemento
     * desta página. Caso contrário, promove o primeiro.
     * @param arquivo Arquivo onde as páginas serão colocadas.
     */
    void promoverElementoPara(
        Pagina *paginaDestino, int indice, Pagina *exPaginaCheia,
        Pagina *novaPagina, bool promoverElementoDoFim, fstream &arquivo)
    {
        int indiceLocal = promoverElementoDoFim ? _tamanho - 1 : 0;

        transferirElementoPara(
            paginaDestino, indice, indiceLocal,
            !promoverElementoDoFim, promoverElementoDoFim);

        // atualiza as páginas no arquivo
        exPaginaCheia->colocarNoArquivo(arquivo);
        novaPagina->colocarNoArquivo(arquivo);

        // Quando um elemento é promovido, o ponteiro da esquerda dele deve apontar
        // para a página que estava cheia (a que provocou a divisão).
        // Já o ponteiro da direita deve apontar para a nova página (gerada pela
        // divisão).
        paginaDestino->ponteiros[indice] = exPaginaCheia->endereco;
        paginaDestino->ponteiros[indice + 1] = novaPagina->endereco;
        paginaDestino->colocarNoArquivo(arquivo);
    }

    /**
     * @brief Transfere a quantidade de dados informada do containerFonte para
     * o containerDestino. A transferência pode ser feita começando a tirar
     * elementos do final do containerFonte ou do início. Isso é controlado
     * pelo parâmetro inicioParaOFim.
     * 
     * @param containerDestino Destino.
     * @param containerFonte Fonte.
     * @param quantidade Quantidade de dados.
     * @param inicioParaOFim Indica se a transferência vai começar pelo início
     * do containerFonte ou pelo fim.
     */
    template <typename ContainerFonte, typename ContainerDestino>
    void transferirPara(
        ContainerDestino &containerDestino,
        ContainerFonte &containerFonte,
        int quantidade, bool inicioParaOFim = false)
    {
        auto inicio = inicioParaOFim ?
            containerFonte.begin() : containerFonte.end() - quantidade;
        auto fim = inicioParaOFim ?
            inicio + quantidade : containerFonte.end();

        containerDestino.insert( // move para o destino usando iteradores sobre a fonte
            containerDestino.end(),
            // move_iterator é usado para mover os elementos invés de copiá-los
            make_move_iterator(inicio),
            make_move_iterator(fim));

        containerFonte.erase(inicio, fim);
    }

    /**
     * @brief Transfere todas as chaves, dados e ponteiros para outra página.
     * Esses dados são pegos da direita para a esquerda nesta página.
     * 
     * @param paginaDestino Página destino.
     */
    void transferirTudoPara(Pagina *paginaDestino)
    {
        transferirPara(paginaDestino->chaves, chaves, _tamanho, true);
        transferirPara(paginaDestino->dados, dados, _tamanho, true);
        transferirPara(paginaDestino->ponteiros, ponteiros, _tamanho + 1, true);

        paginaDestino->_tamanho += _tamanho;
        _tamanho = 0;
    }

    /**
     * @brief Transfere metade das chaves, dos dados e dos ponteiros (com 1 a mais)
     * para outra página. Esses dados são pegos da direita para a esquerda nesta
     * página.
     * 
     * @param paginaDestino Página destino.
     */
    void transferirMetadePara(Pagina *paginaDestino)
    {
        int quantidadeRemovida = _tamanho / 2;

        transferirPara(paginaDestino->chaves, chaves, quantidadeRemovida);
        transferirPara(paginaDestino->dados, dados, quantidadeRemovida);
        transferirPara(paginaDestino->ponteiros, ponteiros, quantidadeRemovida);

        // Pega o último ponteiro desta página e cria uma cópia dele no início
        // da página de destino
        paginaDestino->ponteiros.insert(
            paginaDestino->ponteiros.begin(),
            ponteiros.back());

        paginaDestino->_tamanho += quantidadeRemovida;
        _tamanho -= quantidadeRemovida;
    }

    /**
     * @brief Troca o par (chave, dado) no indiceLocal com o par no indiceDestino.
     * 
     * @param indiceLocal Índice da chave e do dado nesta página.
     * @param paginaDestino Página para onde o par deve ir.
     * @param indiceDestino Índice para onde o par deve ir.
     */
    void swap(int indiceLocal, Pagina* paginaDestino, int indiceDestino)
    {
        iter_swap(chaves.begin() + indiceLocal,
            paginaDestino->chaves.begin() + indiceDestino);

        iter_swap(dados.begin() + indiceLocal,
            paginaDestino->dados.begin() + indiceDestino);
    }

    /**
     * @brief Atualiza a página no arquivo caso ela já tenha um endereço. Caso
     * contrário, adiciona-a ao final do arquivo.
     * 
     * @param arquiv Arquivo onde a página deve ser colocada.
     * 
     * @return file_ptr_type constantes::ptrNuloPagina caso haja algum erro.
     * Caso contrário, retorna o endereço no qual a página foi colocada.
     */
    file_ptr_type colocarNoArquivo(fstream &arquivo)
    {
        if (endereco != constantes::ptrNuloPagina)
        {
            arquivo.seekp(endereco);
        }

        else
        {
            arquivo.seekp(0, fstream::end);
        }

        if (!arquivo.fail())
        {
            endereco = arquivo.tellp();
            arquivo << this;
        }

        return endereco;
    }

    /**
     * @brief Exclui o par (chave, dado) da página no índice informado. É possível
     * excluir também o ponteiro à direita do par.
     * 
     * @param indiceDeExclusao Índice de exclusão.
     * @param excluirPonteiroDaEsquerda Indica se o ponteiro à esquerda do par
     * deve ser excluído.
     * @param excluirPonteiroDaDireita Indica se o ponteiro à direita do par
     * deve ser excluído.
     * 
     * @return true Caso tudo corra bem.
     * @return false Caso a página esteja vazia ou o índice esteja fora do intervalo.
     */
    bool excluir(int indiceDeExclusao, bool excluirPonteiroDaEsquerda = false,
        bool excluirPonteiroDaDireita = false)
    {
        // Só é possível excluir se a página não estiver vazia
        bool sucesso = !vazia() && indiceDeExclusao < _tamanho;

        if (sucesso)
        {
            chaves.erase(chaves.begin() + indiceDeExclusao);
            dados.erase(dados.begin() + indiceDeExclusao);

            if (excluirPonteiroDaEsquerda)
            {
                ponteiros.erase(ponteiros.begin() + indiceDeExclusao);
            }

            if (excluirPonteiroDaDireita)
            {
                ponteiros.erase(ponteiros.begin() + indiceDeExclusao + 1);
            }

            _tamanho--;
        }

        return sucesso;
    }

    /**
     * @brief Imprime, na output stream recebida, cada elemento da página gerando o
     * seguinte formato:
     * 
     * <p>
     * Supondo que:
     * delimitadorEntreOPonteiroEAChave = " (",
     * delimitadorEntreODadoEOPonteiro = ") " e
     * delimitadorEntreAChaveEODado = ", ",
     * 
     * a saída é: 1º ponteiro (1º chave, 1º dado) 2º ponteiro (...
     * </p>
     * 
     * É possível desabilitar a impressão dos dados por meio do parâmetro
     * @p mostrarOsDados.
     * 
     * @param ostream Fluxo de saída onde a página será impressa.
     * @param mostrarOsDados Caso seja true, mostra os dados ligados às chaves.
     * @param delimitadorEntreOPonteiroEAChave Delimitador entre um ponteiro e uma chave.
     * @param delimitadorEntreODadoEOPonteiro Delimitador entre um dado e um ponteiro.
     * @param delimitadorEntreAChaveEODado Delimitador entre uma chave e um dado.
     */
    void mostrar(ostream &ostream = cout,
               bool mostrarOsDados = false,
               string delimitadorEntreOPonteiroEAChave = " (",
               string delimitadorEntreODadoEOPonteiro = ") ",
               string delimitadorEntreAChaveEODado = ", ")
    {
        if (!vazia())
        {
            ostream << "Arquivo[" << (long) endereco << "] = ";
            ostream << (long) ponteiros[0];

            if (mostrarOsDados)
            {
                for (size_t i = 0; i < _tamanho; i++)
                {
                    ostream << delimitadorEntreOPonteiroEAChave << chaves[i];
                    ostream << delimitadorEntreAChaveEODado << dados[i];
                    ostream << delimitadorEntreODadoEOPonteiro << (long) ponteiros[i + 1];
                }
            }

            else
            {
                for (size_t i = 0; i < _tamanho; i++)
                {
                    ostream << delimitadorEntreOPonteiroEAChave << chaves[i];
                    ostream << delimitadorEntreODadoEOPonteiro << (long) ponteiros[i + 1];
                }
            }

            ostream << endl;
        }
    }
};

template <typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
ostream &operator<<(ostream &ostream, PaginaB<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> &pagina)
{
    pagina.mostrar(ostream);

    return ostream;
}

template <typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
fstream &operator>>(fstream &fstream, PaginaB<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> *pagina)
{
    pagina->limpar();
    pagina->setEndereco(fstream.tellg());
    Serializavel *serializavel = pagina;

    return fstream >> serializavel;
}

template <typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
fstream &operator>>(fstream &fstream, PaginaB<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> &pagina)
{
    fstream >> &pagina;
}

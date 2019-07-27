/**
 * @file ArvoreB.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo da classe ArvoreB.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 * 
 * ███████▀▀▀░░░░░░░▀▀▀███████
 * ████▀░░░░░░░░░░░░░░░░░▀████
 * ███│░░░░░░░░░░░░░░░░░░░│███
 * ██▌│░░░░░░░░░░░░░░░░░░░│▐██
 * ██░└┐░░░░░░░░░░░░░░░░░┌┘░██
 * ██░░└┐░░░░░░░░░░░░░░░┌┘░░██
 * ███░┌┘▄▄▄▄▄░░░░░▄▄▄▄▄└┐░███
 * ██▌░│██████▌░░░▐██████│░▐██
 * ███░│▐███▀▀░░▄░░▀▀███▌│░███
 * ███─┘░░░░░░░▐█▌░░░░░░░└─███
 * ███░░░▄▄▄▓░░▀█▀░░▓▄▄░░░████
 * ███▄─┘██▌░░░░░░░░▐██└─▄████
 * █████░░▐█─┬┬┬┬┬┬┬─█▌░░█████
 * ████▌░░░▀┬┼┼┼┼┼┼┼┬▀░░░▐████
 * █████▄░░░└┴┴┴┴┴┴┴┘░░░▄█████
 * ███████▄░░░░░░░░░░░▄███████
 * ██████████▄▄▄▄▄▄▄██████████
 */

#pragma once

#include "templates/tipos.hpp"
#include "templates/debug.hpp"
#include "helpersArvore.hpp"
#include "PaginaB.hpp"

#include <iostream>
#include <fstream>

using namespace std;

/**
 * @brief Classe da árvore B, uma estrutura eficiente para indexamento de registros
 * em disco.
 * 
 * <p>Caso for usar o método print() dá classe, é necessário que a chave e o dado
 * sejam tipos primitivos ou então o operador << deve ser sobrecarregado para que
 * seja possível inserir esses itens num ostream (output stream).</p>
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
template<typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
class ArvoreB
{
public:
    // ------------------------- Typedefs

    /**
     * @brief Padroniza o tipo da página da árvore. Typedefs dentro de classes ou
     * structs são considerados como boa prática em C++.
     */
    typedef PaginaB<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> Pagina;

    // ------------------------- Campos

    const int tamanhoCabecalhoAntesDoEnderecoDaRaiz = 0;
    const int tamanhoCabecalho =
        tamanhoCabecalhoAntesDoEnderecoDaRaiz + sizeof(file_pointer_type);

private:
    // ------------------------- Métodos
    
    void abrirArquivo(string nome)
    {
        arquivo = fstream(nome, fstream::binary | fstream::in | fstream::out);

        if (!arquivo) // Checa se o arquivo não existe ou não está acessível
        {
            // Caso não, cria um arquivo e o fecha
            fstream(nome, fstream::binary | fstream::out).close();
            // Agora sim reabre o arquivo nos modos de leitura e escrita
            arquivo = fstream(nome, fstream::binary | fstream::in | fstream::out);
        }
    }

    /**
     * @brief Vai para o endereço informado e tenta carregar a página.
     * 
     * @param pagina Página a ser carregada.
     * @param endereco Endereço da página.
     * 
     * @return true Caso não haja erros.
     * @return false Caso haja erros.
     */
    bool carregar(Pagina* pagina, file_pointer_type endereco)
    {
        bool sucesso = true;

        arquivo.seekg(endereco);
        arquivo >> pagina; // Carrega a página
        
        if (arquivo.fail())
        {
            sucesso = false;

            // cerr é a saída padrão de erros. Em alguns caso pode ser igual a cout.
            cerr << "[ArvoreB] Não foi possível ler a página do arquivo."
                << endl << "Exceção lançada" << endl;

            throw length_error("[ArvoreB] Não foi possível ler a página do arquivo.");
        }
        
        return sucesso;
    }

    /**
     * @brief Decide em qual página a chave deve ser inserida.
     * 
     * @param irma Página a ser criada.
     * @param filha Página a ser dividida.
     * @param chave Chave a ser inserida.
     * 
     * @return pair<Pagina *, bool> Um par onde o primeiro elemento é a página onde
     * a chave deve ser inserida e o segundo elemento é um booleano que indica se a
     * inserção deve ser feita na página filha.
     */
    pair<Pagina *, bool> obterPaginaDeInsercao(
        Pagina *irma, Pagina *filha, TIPO_DAS_CHAVES &chave)
    {
        // Escolhe a página onde a chave será inserida
        bool inserirNaPaginaFilha = chave <= irma->chaves[0] ? true : false;
        Pagina *paginaDeInsercao = inserirNaPaginaFilha ? filha : irma;

        return pair<Pagina *, bool>(paginaDeInsercao, inserirNaPaginaFilha);
    }

    /**
     * @brief Cria a página irmã e transfere metade da página filha para ela.
     * 
     * @param filha Página a ser dividida.
     * @param irma Página a ser criada.
     * @param chave Chave a ser inserida em alguma dessas páginas.
     * 
     * @return pair<Pagina*, bool> Um par onde o primeiro elemento é a página onde
     * a chave deve ser inserida e o segundo elemento é um booleano que indica se
     * a inserção deve ser na página filha.
     */
    pair<Pagina *, bool> dividir(Pagina *filha, Pagina *irma, TIPO_DAS_CHAVES &chave)
    {
        // Inicia o processo de divisão da página
        irma->limpar(); // Nova página
        filha->transferirMetadePara(irma);

        return obterPaginaDeInsercao(irma, filha, chave);
    }

protected:
    // ------------------------- Campos

    string erro;
    string nomeDoArquivo;
    fstream arquivo;

    int maximoDeBytesParaAChave;
    int maximoDeBytesParaODado;
    int numeroDeChavesPorPagina;
    int ordemDaArvore;

    public: Pagina *paginaPai;
    Pagina *paginaIrmaPai;
    Pagina *paginaFilha;
    Pagina *paginaIrma;
    
    // ------------------------- Métodos

    /**
     * @brief Checa se o arquivo tem tamanho suficiente para ter o cabeçalho
     * da árvore e pelo menos uma página. Caso não, cria um cabeçalho e a raiz
     * da árvore.
     */
    void iniciarArquivoCasoNecessario()
    {
        auto tamanho = obterTamanhoEmBytes(arquivo);
        
        // O arquivo precisa ter pelo menos o cabeçalho da árvore e uma página
        if (tamanho < tamanhoCabecalho + paginaPai->obterTamanhoMaximoEmBytes())
        {
            // Limpa o arquivo e o reabre
            arquivo = fstream(nomeDoArquivo, fstream::binary | fstream::out);
            arquivo = fstream(nomeDoArquivo, fstream::binary | fstream::in | fstream::out);
            
            arquivo.seekp(0); // Coloca o ponteiro de put no início

            // Escreve o endereço da raiz. Ela ficará logo após o endereço dela.
            arquivo << (file_pointer_type)(sizeof(file_pointer_type));
            arquivo << paginaPai; // A página pai está vazia no momento
        }
    }

    /**
     * @brief Obtém o endereço da página recebida por meio da propriedade endereço
     * e o escreve no cabeçalho da árvore como o endereço da nova raiz.
     * 
     * @param novaRaiz Página com um endereço definido.
     */
    void trocarRaizPor(Pagina* novaRaiz)
    {
        if (novaRaiz->obterEndereco() != constantes::ptrNuloPagina)
        {
            arquivo.seekp(tamanhoCabecalhoAntesDoEnderecoDaRaiz);
            arquivo << novaRaiz->obterEndereco();
        }
    }

    /**
     * @brief Promove o par (chave, dado) que estiver sobrando na paginaDeInsercao
     * para a página pai. Caso a página pai esteja cheia, ela é dividida para que
     * o par possa ser promovido.
     * 
     * @param chave Chave do par.
     * @param indiceDePromocao Índice para onde o par (chave, dado) deve ser
     * promovido na página pai.
     * @param paginaDeInsercao Ponteiro para a página que acabou de receber o elemento
     * que causará a promoção.
     * @param inseriuNaPaginaFilha Deve ser true quando o elemento que causará a
     * promoção tiver sido inserido na página filha (a que estava cheia antes de ser
     * dividida). E false quando o elemento tiver sido inserido na página irmã
     * (a que foi gerada na divisão).
     * @param infoPai Considerando que a página pai irá receber um elemento
     * promovido, que ela esteja cheia e não o possa receber, esse parâmetro será
     * preenchido com um par onde o primeiro elemento será um ponteiro para a página
     * que receber o elemento promovido (após a divisão da página pai) e o segundo
     * elemento é um booleano que indica se a promoção foi na página pai ou em sua irmã.
     */
    void promoverOParQueEstiverSobrando(
        int indiceDePromocao, Pagina *paginaDeInsercao,
        bool inseriuNaPaginaFilha, pair<Pagina *, bool> &infoPai)
    {
        Pagina *paginaDestino = paginaPai;
        int indice = indiceDePromocao;

        // Checa se é necessário dividir a página pai antes de promover o par.
        if (paginaPai->cheia())
        {
            // Pega a chave do par que será promovido
            TIPO_DAS_CHAVES& chave = inseriuNaPaginaFilha ?
                paginaFilha->chaves.back() : paginaIrma->chaves[0];

            infoPai = dividir(paginaPai, paginaIrmaPai, chave);
            // Pega a página que receberá o elemento promovido
            paginaDestino = infoPai.first;
            // Obtém o índice no qual o par deve ser promovido
            indice = paginaDestino->obterIndiceDeDescida(chave);
        }

        paginaDeInsercao->promoverElementoPara(
            paginaDestino, indice, // Promove para o índice
            paginaFilha,  // Página que estava cheia
            paginaIrma,   // Página criada
            // Ajuda a saber qual é a posição do elemento que será promovido
            // da paginaDeInsercao.
            inseriuNaPaginaFilha,
            arquivo // Arquivo onde as páginas serão colocadas
        );
    }

    /**
     * @brief Duplica a página filha gerando a página irmã, insere a chave e o dado
     * em alguma delas e promove o elemento que estiver sobrando de acordo com a
     * inserção feita. Caso necessário, duplica a página pai também para que o
     * elemento seja promovido.
     * 
     * @param chave Chave a ser inserida.
     * @param dado Dado a ser inserido.
     * @param indiceDePromocao Índice para onde o par (chave, dado) deve ser
     * promovido na página pai.
     * @param infoPai Considerando que a página pai irá receber um elemento
     * promovido, que ela esteja cheia e não o possa receber, esse parâmetro será
     * preenchido com um par onde o primeiro elemento será um ponteiro para a página
     * que receber o elemento promovido (após a divisão da página pai) e o segundo
     * elemento é um booleano que indica se a promoção foi na página pai ou em sua irmã.
     */
    void tratarPaginaCheia(
        TIPO_DAS_CHAVES &chave, TIPO_DOS_DADOS &dado,
        int indiceDePromocao, pair<Pagina *, bool> &infoPai)
    {
        auto info = dividir(paginaFilha, paginaIrma, chave);
        Pagina *paginaDeInsercao = info.first;
        bool inserirNaPaginaFilha = info.second;

        paginaDeInsercao->inserir(chave, dado);

        promoverOParQueEstiverSobrando(
            indiceDePromocao, paginaDeInsercao,
            inserirNaPaginaFilha, infoPai);
    }

    /**
     * @brief Insere o par (chave, dado) na árvore.
     * 
     * @param chave Chave a ser inserida.
     * @param dado Dado a ser inserido.
     * @param indiceDePromocao Índice na página pai para o qual um elemento desta
     * página seria promovido caso necessário.
     * @param enderecoPaginaFilha Endereço da página a ser carregada.
     * @param enderecoPaginaPai Endereço da página pai.
     * 
     * @return pair<Pagina *, bool> Considerando que a página pai irá receber um
     * elemento promovido, que ela esteja cheia e não o possa receber, retorna um
     * par onde o primeiro elemento será um ponteiro para a página que receber o
     * elemento promovido (após a divisão da página pai) e o segundo elemento é
     * um booleano que indica se a promoção foi na página pai ou em sua irmã. Caso
     * a página pai não esteja cheia ou não vá receber um elemento, retorna um par
     * onde o primeiro elemento é um nullptr e o segundo false.
     */
    pair<Pagina *, bool> inserir(
        TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado,
        int indiceDePromocao, file_pointer_type enderecoPaginaFilha,
        file_pointer_type enderecoPaginaPai)
    {
        pair<Pagina *, bool> infoPai(nullptr, false);

        // Checa se a página foi carregada
        if (carregar(paginaFilha, enderecoPaginaFilha))
        {
            int indiceDeInsercao = paginaFilha->obterIndiceDeDescida(chave);

            // Checa se não há ponteiro de descida. Caso não, a página é uma folha.
            if (paginaFilha->ponteiros[indiceDeInsercao] == constantes::ptrNuloPagina)
            {
                // Checa se a inserção teve sucesso
                if (paginaFilha->inserir(chave, dado, indiceDeInsercao))
                {
                    paginaFilha->colocarNoArquivo(arquivo);
                }

                else // Inserção na página falhou, acontece quando ela está cheia.
                {
                    tratarPaginaCheia(chave, dado, indiceDePromocao, infoPai);
                }
            }
            
            else // A página não é uma folha, continuar descendo
            {
                paginaPai = paginaFilha;

                // O inserir retorna informações sobre a promoção de elementos para
                // esta página
                infoPai = inserir(
                    chave, dado, indiceDeInsercao,
                    paginaFilha->ponteiros[indiceDeInsercao], enderecoPaginaFilha);

                // Checa se esta página precisa promover algum elemento que tenha
                // sido adicionado nela pelas suas filhas
                if (infoPai.first != nullptr)
                {
                    Pagina *paginaDeInsercao = infoPai.first;
                    bool inseriuNaPaginaFilha = infoPai.second;

                    // As páginas que eram pais, na volta da recursividade, são filhas
                    paginaFilha = paginaPai;
                    paginaIrma = paginaIrmaPai;

                    // Checa se não existe um endereço para a página pai. Quando ele
                    // não existir é porque a recursividade voltou à raiz.
                    bool voltouParaARaiz =
                        enderecoPaginaPai == constantes::ptrNuloPagina;

                    if (voltouParaARaiz) paginaPai->limpar();
                    else carregar(paginaPai, enderecoPaginaPai);

                    promoverOParQueEstiverSobrando(
                        indiceDePromocao, paginaDeInsercao,
                        inseriuNaPaginaFilha, infoPai
                    );

                    if (voltouParaARaiz) trocarRaizPor(paginaPai);
                }
            }
        }

        return infoPai;
    }

    file_pointer_type lerEnderecoDaRaiz()
    {
        // Pula as coisas do cabeçalho do arquivo que vierem antes do endereço da raiz
        arquivo.seekg(tamanhoCabecalhoAntesDoEnderecoDaRaiz);

        file_pointer_type endereco;
        arquivo >> endereco; // Carrega o endereço da raiz

        return endereco;
    }

public:
    // ------------------------- Construtores e destrutores

    ArvoreB(string nomeDoArquivo, int ordemDaArvore) :
        nomeDoArquivo(nomeDoArquivo),
        numeroDeChavesPorPagina(ordemDaArvore - 1),
        ordemDaArvore(ordemDaArvore),
        paginaPai(new Pagina(ordemDaArvore) ),
        paginaIrmaPai(new Pagina(ordemDaArvore) ),
        paginaFilha(new Pagina(ordemDaArvore) ),
        paginaIrma(new Pagina(ordemDaArvore) )
    {
        abrirArquivo(nomeDoArquivo);

        obterTamanhoEmBytesDaChaveEDoDado<TIPO_DAS_CHAVES, TIPO_DOS_DADOS>(
            maximoDeBytesParaAChave, maximoDeBytesParaODado
        );

        iniciarArquivoCasoNecessario();
    }

    ~ArvoreB()
    {
        delete paginaPai;
        delete paginaIrmaPai;
        delete paginaFilha;
        delete paginaIrma;
    }

    // ------------------------- Métodos

    /**
     * @brief Insere o par (chave, dado) na árvore.
     * 
     * @param chave Chave a ser inserida.
     * @param dado Dado a ser inserido.
     * 
     * @return true Caso tudo corra bem.
     * @return false Caso um erro ocorra.
     */
    bool inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado)
    {
        inserir(chave, dado, 0, lerEnderecoDaRaiz(), constantes::ptrNuloPagina);

        return true;
    }

    /**
     * @brief Remove o par (chave, dado) da árvore.
     * 
     * @param chave Chave a ser removida.
     * @param dado Dado a ser removido.
     * 
     * @return true Caso tudo corra bem.
     * @return false Caso um erro ocorra.
     */
    bool excluir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado);

    /**
     * @brief Exclui o primeiro registro que for encontrado com a chave informada.
     * 
     * @param chave Chave a ser procurada.
     * 
     * @return TIPO_DOS_DADOS Caso tudo corra bem, retorna o dado correspondente
     * à chave. Caso contrário, retorna
     * 
     * @code{.cpp}
     * TIPO_DOS_DADOS() // Ex.: se os dados são inteiros, retorna int(), que é 0.
     * @endcode
     * 
     * Em casos de erro, uma flag interna é ativada. Dessa forma, você pode usar
     * qualquer um dos dois ifs abaixo para checar erros:
     * 
     * @code{.cpp}
     * if (ArvoreB.excluir(chave) == TIPO_DOS_DADOS()) ArvoreB.printError();
     * if (ArvoreB.erro) ArvoreB.printError();
     * @endcode
     */
    TIPO_DOS_DADOS excluir(TIPO_DAS_CHAVES chave);
    
    /**
     * @brief Exclui todos os registros que forem encontrados com a chave informada.
     * 
     * @param chave Chave a ser procurada.
     * 
     * @return vector<TIPO_DOS_DADOS> Vetor com cada dado correspondente à chave.
     */
    vector<TIPO_DOS_DADOS> excluirRegistrosComAChave(TIPO_DAS_CHAVES chave);

    /**
     * @brief Procura o primeiro registro com a chave informada e pega o dado
     * correspondente à ela.
     * 
     * @param chave Chave a ser procurada.
     * 
     * @return TIPO_DOS_DADOS Caso tudo corra bem, retorna o dado correspondente
     * à chave. Caso contrário, retorna
     * 
     * @code{.cpp}
     * TIPO_DOS_DADOS() // Ex.: se os dados são inteiros, retorna int(), que é 0.
     * @endcode
     * 
     * Em casos de erro, uma flag interna é ativada. Dessa forma, você pode usar
     * qualquer um dos dois ifs abaixo para checar erros:
     * 
     * @code{.cpp}
     * if (ArvoreB.excluir(chave) == TIPO_DOS_DADOS()) ArvoreB.printError();
     * if (ArvoreB.erro) ArvoreB.printError();
     * @endcode
     */
    TIPO_DOS_DADOS pesquisar(TIPO_DAS_CHAVES chave);
    
    /**
     * @brief Procura todos os registros que forem encontrados com a chave informada.
     * 
     * @param chave Chave a ser procurada.
     * 
     * @return vector<TIPO_DOS_DADOS> Vetor com cada dado correspondente à chave.
     */
    vector<TIPO_DOS_DADOS> listarDadosComAChave(TIPO_DAS_CHAVES chave);

    /**
     * @brief Procura todos os registros que forem encontrados com a chave entre
     * as chaves informadas. Inclui as próprias chaves. O intervalo é
     * [chaveMenor, chaveMaior].
     * 
     * <p><b>Caso TIPO_DAS_CHAVES e TIPO_DOS_DADOS forem primitivos, ignore o
     * próximo parágrafo.</b></p>
     * 
     * <p><b>Esta função exige que TIPO_DAS_CHAVES e TIPO_DOS_DADOS sobrecarreguem
     * os operadores <= (menor ou igual) e >= (maior ou igual).</b></p>
     * 
     * @param chaveMenor Valor do limite inferior.
     * @param chaveMaior Valor do limite superior.
     * 
     * @return vector<TIPO_DOS_DADOS> Vetor com cada dado correspondente à chave.
     */
    vector<TIPO_DOS_DADOS> listarDadosComAChaveEntre(
        TIPO_DAS_CHAVES chaveMenor,
        TIPO_DAS_CHAVES chaveMaior);

    /**
     * @brief Imprime, na saída padrão, uma representação vertical da árvore.
     * A saída é similar à do comando "tree /f" do windows.
     */
    void print();

    void printTeste(Pagina *paginaAuxiliar, file_pointer_type endereco)
    {
        if (carregar(paginaAuxiliar, endereco))
        {
            cout << *paginaAuxiliar;

            for (auto &&i : paginaAuxiliar->ponteiros)
            {
                if (i != constantes::ptrNuloPagina)
                {
                    printTeste(paginaAuxiliar, i);
                }
            }
        }
    }

    void printTeste()
    {
        Pagina *pagina = new Pagina(ordemDaArvore);

        printTeste(pagina, lerEnderecoDaRaiz());

        delete pagina;
    }
};
/**
 * @file PaginaB.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo da classe PaginaB.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#include "templates/tipos.hpp"
#include "templates/serializavel.hpp"

#include <iostream>
#include <algorithm>

using namespace std;

namespace constantes
{
    static const file_pointer_type ptrNuloPagina = -1;
}

/**
 * @brief Classe com as características da página da árvore B.
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
class PaginaB : public Serializavel
{
protected:
    // ------------------------- Campos

    int numeroDeElementos;
	int maximoDeBytesParaAChave;
	int maximoDeBytesParaODado;
	int numeroDeChavesPorPagina;
	int ordemDaArvore;

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
    vector<file_pointer_type> ponteiros;

    // ------------------------- Construtores

    PaginaB() : numeroDeElementos(0) { }

    /**
     * @brief Constrói uma nova página com a ordem informada.
     * 
     * @param ordemDaArvore Ordem da árvore B (quantidade máxima de filhos por página).
     * @param maximoDeBytesParaAChave Quantidade máxima de bytes que a chave pode gastar
     * nos registros.  Valor padrão = sizeof(TIPO_DAS_CHAVES).
     * @param maximoDeBytesParaODado Quantidade máxima de bytes que o dado pode gastar
     * nos registros. Valor padrão = sizeof(TIPO_DOS_DADOS).
     */
    PaginaB(int ordemDaArvore,
        int maximoDeBytesParaAChave,
        int maximoDeBytesParaODado) :

        numeroDeElementos(0),
        maximoDeBytesParaAChave(maximoDeBytesParaAChave),
        maximoDeBytesParaODado(maximoDeBytesParaODado),
        numeroDeChavesPorPagina(ordemDaArvore - 1),
        ordemDaArvore(ordemDaArvore)
    {
        chaves.reserve(numeroDeChavesPorPagina);
        dados.reserve(numeroDeChavesPorPagina);
        ponteiros.reserve(ordemDaArvore);

        fill(ponteiros.begin(), ponteiros.end(), constantes::ptrNuloPagina);
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
     * nos registros.  Valor padrão = sizeof(TIPO_DAS_CHAVES).
     * @param maximoDeBytesParaODado Quantidade máxima de bytes que o dado pode gastar
     * nos registros. Valor padrão = sizeof(TIPO_DOS_DADOS).
     */
    PaginaB(DataInputStream& bytes, int ordemDaArvore) : PaginaB(ordemDaArvore)
    {
        lerBytes(bytes);
    }

    // ------------------------- Métodos herdados de Serializavel

    virtual int obterTamanhoMaximoEmBytes() override
    {
        // bytes para guardar a quantidade de elementos na página
        return sizeof( decltype(numeroDeElementos) ) + // falta link aqui
            ordemDaArvore * sizeof(file_pointer_type) + // bytes para os ponteiros
            numeroDeChavesPorPagina * maximoDeBytesParaAChave + // bytes para as chaves
            numeroDeChavesPorPagina * maximoDeBytesParaODado; // bytes para os dados
    }

    virtual DataOutputStream& gerarDataOutputStream(DataOutputStream& out) override
    {
        // É necessário que a chave e o dado sejam tipos primitivos ou que
        // herdem a classe Serializavel e tenham um construtor sem parâmetros.
        TIPO_DAS_CHAVES chave;
        TIPO_DOS_DADOS dado;

        out << numeroDeElementos << ponteiros.at(0);

        for (size_t i = 0; i < numeroDeChavesPorPagina; i++)
        {
            chave = chaves.at(i);
            dado = dados.at(i);

            out << chave;
            out << dado;
            out << ponteiros.at(i + 1);
        }

        return out;
    }

    virtual void lerBytes(DataInputStream& input) override
    {
        // É necessário que a chave e o dado sejam tipos primitivos ou que
        // herdem a classe Serializavel e tenham um construtor sem parâmetros.
        TIPO_DAS_CHAVES chave;
        TIPO_DOS_DADOS dado;
        file_pointer_type ponteiro;

        input >> numeroDeElementos >> ponteiro;
        ponteiros[0] = ponteiro;
        
        for (size_t i = 0; i < numeroDeChavesPorPagina; i++)
        {
            input >> chave;
            input >> dado;
            input >> ponteiro;

            chaves[i] = chave;
            dados[i] = dado;
            ponteiros[i + 1] = ponteiro;
        }
    }

    // ------------------------- Métodos

    /**
     * @brief Retorna 0 (zero) caso a página não tenha sido carregada do arquivo ainda.
     * Caso contrário, retorna o seu número de elementos.
     * 
     * @return int O número de elementos da página.
     */
    int obterNumeroDeElementos()
    {
        return numeroDeElementos;
    }

    /**
     * @brief Checa se a página está sem elementos.
     * 
     * @return true Caso não haja elementos na página.
     * @return false Caso haja elementos na página.
     */
    bool vazia()
    {
        return numeroDeElementos == 0;
    }

    /**
     * @brief Checa se a página está cheia.
     * 
     * @return true Caso a página esteja cheia.
     * @return false Caso a página não esteja cheia.
     */
    bool cheia()
    {
        return numeroDeElementos == numeroDeChavesPorPagina;
    }

    /**
     * @brief Zera a quantidade de elementos e limpa todos os vetores internos.
     */
    void limpar()
    {
        numeroDeElementos = 0;

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
    int obterIndiceDeInsercao(TIPO_DAS_CHAVES chave)
    {
        // lower_bound (pesquisa binária) -> http://www.cplusplus.com/reference/algorithm/lower_bound/
        auto iteradorDeInsercao = lower_bound(chaves.begin(), chaves.begin() + chaves.size(), chave);

        return iteradorDeInsercao - chaves.begin();
    }

    /**
     * @brief Insere o par (chave, dado) na página no índice informado.
     * 
     * @param chave Chave do par.
     * @param dado Dado do par.
     * @param indiceDeInsercao Índice de inserção do par.
     * 
     * @return true Caso tudo corra bem.
     * @return false Caso a página esteja cheia.
     */
    bool inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado, int indiceDeInsercao)
    {
        bool sucesso = !cheia(); // Só é possível inserir se a página não estiver cheia

		if (sucesso)
		{
            chaves.insert(indiceDeInsercao, chave);
            dados.insert(indiceDeInsercao, dado);
            ponteiros.insert(indiceDeInsercao + 1, constantes::ptrNuloPagina);
            
            numeroDeElementos++;
		}

        return sucesso;
    }

    /**
     * @brief Insere o par (chave, dado) na página de forma ordenada.
     * 
     * @param chave Chave do par.
     * @param dado Dado do par.
     * 
     * @return true Caso tudo corra bem.
     * @return false Caso a página esteja cheia.
     */
    bool inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado)
    {
        return inserir(chave, dado, obterIndiceDeInsercao(chave));
    }

    void transferirElementoPara(Pagina* paginaDestino, int indiceNoDestino, int indiceLocal)
    {
        paginaDestino->inserir(chaves[indiceLocal], dados[indiceLocal], indiceNoDestino);

        chaves.erase(indiceLocal);
        dados.erase(indiceLocal);
    }

	/**
	 * @brief Pega o elemento mais à direita desta página e o promove para
	 * o índice informado na página destino.
	 * 
     * @param pagina Página destino.
	 * @param indice Índice de inserção na página destino.
	 */
	void promoverElementoParaAPagina(Pagina* pagina, int indice)
	{
		int numeroDeElementosDaPagina = pagina->obterNumeroDeElementos();

		pagina->inserir(
			chaves[numeroDeElementosDaPagina],
			dados[numeroDeElementosDaPagina],
			indice
		);
	}

    /**
     * @brief Transfere metade das chaves e dos dados para outra página.
     * 
     * @param paginaDestino Página destino.
     */
    template<typename ContainerFonte, typename ContainerDestino>
    void transferirMetadePara(ContainerDestino& containerDestino, ContainerFonte& containerFonte, int quantidade)
    {
        auto inicio = containerFonte.end() - quantidade;
        auto fim = containerFonte.end();

        containerDestino.insert( // move para o destino usando iteradores sobre a fonte
            containerDestino.end(),
            // move_iterator é usado para mover os elementos invés de copiá-los
            make_move_iterator(inicio),
            make_move_iterator(fim)
        );

        containerFonte.erase(inicio, fim);
    }

    /**
     * @brief Transfere metade das chaves e dos dados para outra página.
     * 
     * @param paginaDestino Página destino.
     */
    void transferirMetadePara(Pagina* paginaDestino)
    {
        int quantidadeRemovida = numeroDeElementos / 2;
        
        transferirMetadePara(paginaDestino->chaves, chaves, quantidadeRemovida);
        transferirMetadePara(paginaDestino->dados, dados, quantidadeRemovida);
        transferirMetadePara(paginaDestino->ponteiros, ponteiros, quantidadeRemovida + 1); // falta aqui

        numeroDeElementos -= quantidadeRemovida;
        paginaDestino->numeroDeElementos += quantidadeRemovida;
    }

    /**
     * @brief Imprime, na output stream recebida, cada elemento da página gerando o
     * seguinte formato:
     * 
     * <p>
     * 1º ponteiro <delimitadorEntreOsItens> 1º chave <delimitadorEntreOsItens>
     * 1º dado <delimitadorEntreOsItens> 2º ponteiro <delimitadorEntreOsItens> ...
     * </p>
     * 
     * @param delimitadorEntreOsItens Delimitador entre cada elemento da página.
     * Valor padrão = " ".
     * @param ostream Fluxo de saída onde a página será impressa.
     * Valor padrão = std::cout.
     */
    void print(string delimitadorEntreOsItens = " ", ostream& ostream = cout)
    {
        if (!vazia())
        {
            ostream << ponteiros[0];

            for (size_t i = 0; i < numeroDeElementos; i++)
            {
                ostream << delimitadorEntreOsItens << chaves[i];
                ostream << delimitadorEntreOsItens << dados[i];
                ostream << delimitadorEntreOsItens << ponteiros[i + 1];
            }

            ostream << endl;
        }
    }
};

template<typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
ostream& operator<< (ostream& ostream, PaginaB<TIPO_DAS_CHAVES, TIPO_DOS_DADOS>& pagina)
{
    pagina.print(" ", ostream);

    return ostream;
}

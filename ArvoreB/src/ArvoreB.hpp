/**
 * @file ArvoreB.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo da classe ArvoreB.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
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

    /**
     * @brief Padroniza o tipo da tripla (chave, dado, ponteiro).
     */
    typedef struct Tripla<TIPO_DAS_CHAVES, TIPO_DOS_DADOS, file_pointer_type> Tripla;

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
     * @brief Atualiza a página no arquivo caso ela já tenha um endereço. Caso
     * contrário, adiciona-a ao final do arquivo.
     * 
     * @param pagina Página a ser colocada no arquivo.
     * 
     * @return file_pointer_type constantes::ptrNuloPagina caso haja algum erro.
     * Caso contrário, retorna o endereço no qual a página foi colocada.
     */
    file_pointer_type colocarNoArquivo(Pagina* pagina)
    {
        file_pointer_type endereco = constantes::ptrNuloPagina;
        file_pointer_type enderecoPagina = pagina->obterEndereco();

        if (enderecoPagina != constantes::ptrNuloPagina)
        {
            arquivo.seekp(enderecoPagina);
        }

        else
        {
            arquivo.seekp(0, fstream::end);
        }

        if (!arquivo.fail())
        {
            endereco = arquivo.tellp();
            arquivo << pagina;
        }

        return endereco;
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
     * @brief Cria a página irmã e transfere metade da página filha para ela.
     * Depois, insere a chave e o dado em alguma delas de acordo com a ordenação.
     * A inserção da chave e do dado pode ser ignorada por meio do parâmetro
     * @p inserir.
     * 
     * @param filha Página a ser "duplicada".
     * @param irma Página a ser criada.
     * @param chave Chave a ser inserida.
     * @param dado Dado a ser inserido.
     * @param inserir Diz se a inserção da chave e do dado realmente deve ser feita.
     * 
     * @return pair<Pagina*, bool> Um par onde o primeiro elemento é a página onde
     * a chave e o dado foram inseridos e o segundo elemento é um booleano que
     * indica se a inserção foi na página irmã.
     */
    pair<Pagina*, bool> criar(
        Pagina* irma, Pagina* filha,
        TIPO_DAS_CHAVES& chave, TIPO_DOS_DADOS& dado,
        bool inserir = true)
    {
        // Inicia o processo de duplicação da página
        irma->limpar(); // Nova página
        filha->transferirMetadePara(irma);

        // Escolhe a página onde a chave será inserida
        bool inserirNaPaginaIrma = chave > filha->chaves.back() ? true : false;
        Pagina* paginaDeInsercao = inserirNaPaginaIrma ? irma : filha;

        if (inserir) paginaDeInsercao->inserir(chave, dado);

        return pair<Pagina*, bool>(paginaDeInsercao, inserirNaPaginaIrma);
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

    Pagina *paginaPai;
    Pagina *paginaIrmaPai;
    Pagina *paginaFilha;
    Pagina *paginaIrma;

    // ------------------------- Métodos

    /**
     * @brief Duplica a página filha gerando a página irmã, insere a chave e o dado
     * em alguma delas e promove o elemento de acordo com a inserção feita. Caso
     * necessário, duplica a página pai também para que o elemento seja promovido.
     * 
     * @param chave Chave a ser inserida.
     * @param dado Dado a ser inserido.
     * @param infoPai Um par onde o primeiro elemento será um ponteiro para a página
     * onde foi promovido o elemento após a duplicação da página pai (caso aconteça).
     */
    void tratarPaginaCheia(
        TIPO_DAS_CHAVES &chave, TIPO_DOS_DADOS &dado, pair<Pagina *, bool>& infoPai)
    {
        auto info = criar(paginaIrma, paginaFilha, chave, dado);
        Pagina *paginaDeInsercao = info.first;
        bool inserirNaNovaPagina = info.second;

        // Checa se é necessário duplicar a página pai antes de
        // promover um elemento.
        if (paginaPai->cheia())
        {
            infoPai = criar(paginaIrmaPai, paginaPai, chave, dado, false);
        }

        paginaDeInsercao->promoverElementoPara(
            paginaPai, 0, // Promove para a posição 0
            paginaFilha,  // Página que estava cheia
            paginaIrma,   // Página criada
            // Ajuda a saber qual é a posição do elemento que será
            // promovido desta página. (paginaDeInsercao).
            inserirNaNovaPagina
        );
    }

    /**
     * @brief Insere o par (chave, dado) na árvore.
     * 
     * @param chave Chave a ser inserida.
     * @param dado Dado a ser inserido.
     * @param enderecoPaginaFilha Endereço da página a ser carregada.
     * 
     * @return pair<Pagina *, bool> Um par onde o primeiro elemento será um ponteiro
     * para a página onde foi promovido o elemento após a duplicação da página pai
     * (caso aconteça).
     */
    pair<Pagina *, bool> inserir(
        TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado,
        file_pointer_type enderecoPaginaFilha)
    {
        pair<Pagina *, bool> infoPai(nullptr, false);

        // Checa se a página foi carregada
        if (carregar(paginaFilha, enderecoPaginaFilha))
        {
            int indiceDeInsercao = paginaFilha->obterIndiceDeDescida(chave);

            // Checa se não há ponteiro de descida. Caso não, a página é uma folha.
            if (paginaFilha->ponteiros[indiceDeInsercao] == constantes::ptrNuloPagina)
            {
                // Checa se a inserção na página falhou. Acontece quando ela está cheia.
                if (!paginaFilha->inserir(chave, dado, indiceDeInsercao))
                {
                    tratarPaginaCheia(chave, dado, infoPai);
                    // atualiza as páginas no arquivo
                    colocarNoArquivo(paginaPai);
                    colocarNoArquivo(paginaIrma);
                }

                colocarNoArquivo(paginaFilha);
            }
            
            else // A página não é uma folha, continuar descendo
            {
                paginaPai = paginaFilha;

                infoPai = inserir(
                    chave, dado, paginaFilha->ponteiros[indiceDeInsercao]);

                // Checa se esta página precisa promover algum elemento que tenha
                // sido adicionado nela pelas suas filhas
                if (infoPai.first != nullptr)
                {
                    // olhar aqui
                }
            }
        }

        return infoPai;
    }

public:
    // ------------------------- Campos

    const int tamanhoCabecalhoAntesDaRaiz = 0;
    const int tamanhoCabecalho = tamanhoCabecalhoAntesDaRaiz + sizeof(file_pointer_type);

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
        // Pula as coisas do cabeçalho do arquivo que vierem antes do endereço da raiz
        arquivo.seekg(tamanhoCabecalhoAntesDaRaiz);
        
        file_pointer_type endereco;
        arquivo >> endereco; // Carrega o endereço da raiz

        inserir(chave, dado, endereco);

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
};
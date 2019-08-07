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
#include "helpersArvore.hpp"
#include "PaginaB.hpp"

#include <iostream>
#include <fstream>
#include <list>

using namespace std;

/**
 * @brief Classe da árvore B, uma estrutura eficiente para indexamento de registros
 * em disco.
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
        tamanhoCabecalhoAntesDoEnderecoDaRaiz + sizeof(file_ptr_type);

protected:
    // ------------------------- Campos

    string msgErro;
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

    void atribuirErro(string msgErro)
    {
        this->msgErro = "[ArvoreB]: ";
        this->msgErro.append(msgErro);
    }

    void limparErro()
    {
        msgErro = "";
    }

    void mostrarErro()
    {
        cout << msgErro << endl;
    }

    bool erro()
    {
        return msgErro.empty();
    }

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
    bool carregar(Pagina *pagina, file_ptr_type endereco)
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
            arquivo = fstream(nomeDoArquivo, fstream::binary | fstream::trunc);
            arquivo = fstream(nomeDoArquivo, fstream::binary | fstream::in | fstream::out);
            
            arquivo.seekp(0); // Coloca o ponteiro de put no início

            // Escreve o endereço da raiz. Ela ficará logo após o endereço dela.
            arquivo << (file_ptr_type)(sizeof(file_ptr_type));
            arquivo << paginaPai; // A página pai está vazia no momento
        }
    }

    /**
     * @brief Procura o primeiro registro com a chave informada.
     * 
     * @param chave Chave a ser procurada.
     * @param indiceDeDescida Índice do ponteiro na página pai que levou a esta página.
     * @param enderecoPaginaFilha Endereço da página a ser carregada.
     * @param parDoCaminho Par onde o primeiro elemento será uma lista com todos os
     * endereços de todas as páginas pelas quais a recursividade passar e o segundo
     * elemento será uma lista com todos os índices dos ponteiros que a recursividade
     * acessar para descer de uma página para a outra.
     * @param irAteAFolha Indica se a pesquisa não deve parar caso a chave seja
     * encontrada em páginas que não sejam folhas.
     */
    void obterCaminhoDeDescida(
        TIPO_DAS_CHAVES &chave,
        int indiceDeDescida,
        file_ptr_type enderecoPaginaFilha,
        pair< list<file_ptr_type>, list<int> > &parDoCaminho,
        bool irAteAFolha = false)
    {
        list<file_ptr_type> &caminho = parDoCaminho.first;
        list<int> &indices = parDoCaminho.second;

        // Empilha o endereço da página que vai ser carregada e o índice do
        // ponteiro na página pai que nos trouxe a ela
        caminho.push_back(enderecoPaginaFilha);
        indices.push_back(indiceDeDescida);

        // Checa se a página foi carregada
        if (carregar(paginaFilha, enderecoPaginaFilha))
        {
            int indiceDeDescida = paginaFilha->obterIndiceDeDescida(chave);
            int indiceDaChave = indiceDeDescida == 0 ? 0 : indiceDeDescida - 1;
            file_ptr_type ponteiroDeDescida = paginaFilha->ponteiros[indiceDeDescida];

            // Checa se há ponteiro de descida e se a pesquisa deve ir
            // obrigatoriamente até uma folha ou se a chave não foi encontrada.
            if (ponteiroDeDescida != constantes::ptrNuloPagina &&
                (irAteAFolha || paginaFilha->chaves[indiceDaChave] != chave))
            {
                // A página filha passa a ser pai. O swap é necessário pois cada
                // um desses ponteiros aponta para um objeto página concreto e a
                // referência para este objeto não pode ser perdida.
                swap(paginaFilha, paginaPai);

                obterCaminhoDeDescida(
                    chave, indiceDeDescida, ponteiroDeDescida,
                    parDoCaminho, irAteAFolha);
            }
        }
    }

    /**
     * @brief Procura o primeiro registro com a chave informada.
     * 
     * @param chave Chave a ser procurada.
     * @param indiceDeDescida Índice do ponteiro na página pai que levou a esta página.
     * @param enderecoPaginaFilha Endereço da página a ser carregada.
     * @param irAteAFolha Indica se a pesquisa não deve parar caso a chave seja
     * encontrada em páginas que não sejam folhas.
     * 
     * @return pair< list<file_ptr_type>, list<int> > Um par onde o primeiro
     * elemento será uma lista com todos os endereços de todas as páginas pelas quais
     * a recursividade passar e o segundo elemento será uma lista com todos os índices
     * dos ponteiros que a recursividade acessar para descer de uma página para a
     * outra.
     */
    pair< list<file_ptr_type>, list<int> > obterCaminhoDeDescida(
        TIPO_DAS_CHAVES &chave,
        int indiceDeDescida,
        file_ptr_type enderecoPaginaFilha,
        bool irAteAFolha = false)
    {
        pair< list<file_ptr_type>, list<int> > parDoCaminho;

        obterCaminhoDeDescida(
            chave, indiceDeDescida, enderecoPaginaFilha, parDoCaminho, irAteAFolha);

        return parDoCaminho;
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
     * @brief Tenta pegar uma chave da página no endereço informado e colocar na
     * paginaFilha.
     * 
     * @param enderecoDaPagina Endereço da página que perderá uma chave.
     * @param indiceDeDescida Índice do ponteiro na página pai que foi usado para
     * chegar na paginaFilha.
     * @param pegarChaveDoFim Indica se a chave a ser pega fica no fim da página
     * do endereço informado ou no início.
     * 
     * @return true Caso a chave seja transferida com sucesso.
     * @return false Caso a chave não seja transferida.
     */
    bool pegarChaveDaPagina(
        file_ptr_type enderecoDaPagina, int indiceDeDescida, bool pegarChaveDoFim)
    {
        bool sucesso = false;

        if (carregar(paginaIrma, enderecoDaPagina) &&
            paginaIrma->tamanho() > numeroDeChavesPorPagina / 2)
        {
            int indiceDaChavePai =
                pegarChaveDoFim ? indiceDeDescida - 1 : indiceDeDescida;

            int indiceNaPaginaIrma = pegarChaveDoFim ? paginaIrma->tamanho() - 1 : 0;
            int indiceNaPaginaFilha = pegarChaveDoFim ? 0 : paginaFilha->tamanho();

            paginaPai->transferirElementoPara(
                paginaFilha, indiceNaPaginaFilha, indiceDaChavePai);

            paginaIrma->transferirElementoPara(
                paginaPai, indiceDaChavePai, indiceNaPaginaIrma,
                !pegarChaveDoFim, pegarChaveDoFim, false);

            paginaPai->colocarNoArquivo(arquivo);
            paginaIrma->colocarNoArquivo(arquivo);

            sucesso = true;
        }
        
        return sucesso;
    }

    /**
     * @brief Tenta pegar uma chave emprestada das páginas irmãs.
     * 
     * @param indiceDeDescida Índice do ponteiro na página pai que foi usado para
     * chegar na paginaFilha.
     * 
     * @return true Caso uma chave tenha sido pega.
     * @return false Caso as irmãs não puderem emprestar.
     */
    bool pegarChaveEmprestada(int indiceDeDescida)
    {
        bool pegouEmprestado = indiceDeDescida > 0 &&
            pegarChaveDaPagina(paginaPai->ponteiros[indiceDeDescida - 1],
                indiceDeDescida, true);
        
        pegouEmprestado = pegouEmprestado ||
            indiceDeDescida < paginaPai->tamanho() &&
            pegarChaveDaPagina(paginaPai->ponteiros[indiceDeDescida + 1],
                indiceDeDescida, false);

        return pegouEmprestado;
    }

    /**
     * @brief Funde a paginaFilha com uma de suas irmãs e também com a chave na
     * página pai.
     * 
     * @param enderecoDaPagina Endereço da página a ser fundida com a paginaFilha.
     * @param indiceDeDescida Índice do ponteiro na página pai que foi usado para
     * chegar na paginaFilha.
     * @param fundirDireita Indica se a fusão da paginaFilha será com a sua irmã
     * da direita.
     * 
     * @return true Caso a fusão ocorra com sucesso.
     * @return false Caso a fusão não ocorra.
     */
    bool fundirCom(
        file_ptr_type enderecoDaPagina, int indiceDeDescida, bool fundirDireita)
    {
        bool sucesso = false;

        if (carregar(paginaIrma, enderecoDaPagina))
        {
            if (fundirDireita)
            {
                paginaPai->transferirElementoPara(
                    paginaFilha, paginaFilha->tamanho(), indiceDeDescida,
                    false, true, false);
                    
                paginaIrma->transferirTudoPara(paginaFilha);
            }

            else
            {
                paginaPai->transferirElementoPara(
                    paginaIrma, paginaIrma->tamanho(), indiceDeDescida - 1,
                    false, true, false);
                    
                paginaFilha->transferirTudoPara(paginaIrma);
            }

            paginaPai->colocarNoArquivo(arquivo);
            paginaIrma->colocarNoArquivo(arquivo);

            sucesso = true;
        }
        
        return sucesso;
    }

    /**
     * @brief Funde a paginaFilha com uma de suas irmãs e com uma chave da página pai.
     * 
     * @param indiceDeDescida Índice do ponteiro na página pai que foi usado para
     * chegar na paginaFilha.
     */
    void fundirPaginas(int indiceDeDescida)
    {
        bool fundiu = indiceDeDescida < paginaPai->tamanho() &&
            fundirCom(paginaPai->ponteiros[indiceDeDescida + 1],
                indiceDeDescida, true);
        
        fundiu = fundiu ||
            indiceDeDescida > 0 &&
            fundirCom(paginaPai->ponteiros[indiceDeDescida - 1],
                indiceDeDescida, false);
    }

    /**
     * @brief Troca a chave no índice informado da paginaFilha pela sua antecessora.
     * 
     * @param indiceDaChave Índice da chave que será trocada pela sua antecessora.
     * @param pilhaDeEnderecos Uma pilha com todos os endereços de todas as páginas
     * pelas quais a recursividade passou até o momento.
     * @param pilhaDeIndices Uma pilha com todos os índices dos ponteiros que a
     * recursividade tenha acessado para descer de uma página para a outra.
     */
    void trocarChavePorAntecessora(int indiceDaChave,
        list<file_ptr_type>& pilhaDeEnderecos, list<int>& pilhaDeIndices)
    {
        file_ptr_type enderecoFilha = paginaFilha->ponteiros[indiceDaChave];

        swap(paginaPai, paginaFilha); // Guarda a página filha na página pai
        carregar(paginaFilha, enderecoFilha);

        pilhaDeEnderecos.push_back(enderecoFilha);
        pilhaDeIndices.push_back(indiceDaChave);

        while (!paginaFilha->eUmaFolha())
        {
            enderecoFilha = paginaFilha->ponteiros[paginaFilha->tamanho() + 1];
            carregar(paginaFilha, enderecoFilha);

            pilhaDeEnderecos.push_back(enderecoFilha);
            pilhaDeIndices.push_back(paginaFilha->tamanho() + 1);
        }

        paginaFilha->swap(paginaFilha->tamanho() - 1, paginaPai, indiceDaChave);
        // Salva as páginas cuja chaves foram trocadas
        paginaPai->colocarNoArquivo(arquivo);
        paginaFilha->colocarNoArquivo(arquivo);

        pilhaDeEnderecos.pop_back(); // Retira o endereço da filha para recuperar a pai
        carregar(paginaPai, pilhaDeEnderecos.back()); // Carrega a pai
        pilhaDeEnderecos.push_back(enderecoFilha); // Volta com o endereço da filha
    }
    
    /**
     * @brief Exclui o primeiro registro que for encontrado com a chave informada.
     * 
     * @param chave Chave a ser procurada.
     * @param pilhaDeEnderecos Uma pilha com todos os endereços de todas as páginas
     * pelas quais a recursividade passou até o momento.
     * @param pilhaDeIndices Uma pilha com todos os índices dos ponteiros que a
     * recursividade tenha acessado para descer de uma página para a outra.
     * 
     * @return TIPO_DOS_DADOS Caso tudo corra bem, retorna o dado correspondente
     * à chave. Caso contrário, retorna
     * 
     * @code{.cpp}
     * TIPO_DOS_DADOS() // Ex.: se os dados são inteiros, retorna int(), que é 0.
     * @endcode
     * 
     * Em casos onde a chave não é encontrada, uma flag interna é ativada. Dessa
     * forma, você pode usar qualquer um dos dois ifs abaixo para checar erros:
     * 
     * @code{.cpp}
     * if (ArvoreB.excluir(chave) == TIPO_DOS_DADOS()) ArvoreB.mostrarErro();
     * if (ArvoreB.erro()) ArvoreB.mostrarErro();
     * @endcode
     */
    TIPO_DOS_DADOS excluir(TIPO_DAS_CHAVES &chave,
        list<file_ptr_type>& pilhaDeEnderecos, list<int>& pilhaDeIndices)
    {
        TIPO_DOS_DADOS dadoExcluido;
        // paginaFilha aponta para a última página carregada na descida da árvore
        // (a página onde a chave foi encontrada ou alguma folha).
        int indiceDaChave = paginaFilha->obterIndiceDeDescida(chave);

        // Checa se a chave realmente foi encontrada
        if (paginaFilha->chaves[indiceDaChave] == chave)
        {
            dadoExcluido = paginaFilha->dados[indiceDaChave];
            limparErro();

            if (paginaFilha->eUmaFolha())
            {
                paginaFilha->excluir(indiceDaChave, false, true);

                // Checa se o tamanho da paginaFilha antes da remoção era <= a 50%
                if (paginaFilha->tamanho() + 1 <= numeroDeChavesPorPagina / 2)
                {
                    // Obtém o índice do ponteiro na página pai que foi usado para
                    // chegar na paginaFilha
                    int indiceDeDescida = pilhaDeIndices.back();

                    if (!pegarChaveEmprestada(indiceDeDescida))
                    {
                        fundirPaginas(indiceDeDescida);
                        pilhaDeEnderecos.pop_back();
                        
                        while (paginaPai->tamanho() < numeroDeChavesPorPagina / 2
                            && pilhaDeEnderecos.size() > 1)
                        {
                            swap(paginaPai, paginaFilha);
                            pilhaDeIndices.pop_back();
                            pilhaDeEnderecos.pop_back();
                            carregar(paginaPai, pilhaDeEnderecos.back());
                            fundirPaginas(pilhaDeIndices.back());
                        }

                        if (pilhaDeEnderecos.size() == 1 && paginaPai->vazia())
                            trocarRaizPor(paginaFilha);
                    }
                }

                paginaFilha->colocarNoArquivo(arquivo);
            }

            else
            {
                trocarChavePorAntecessora(
                    indiceDaChave, pilhaDeEnderecos, pilhaDeIndices);
                excluir(chave, pilhaDeEnderecos, pilhaDeIndices);
            }
        }

        else atribuirErro("A chave não foi encontrada");

        return dadoExcluido;
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

        else infoPai.first = nullptr;

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
     * @brief Divide a página filha gerando a página irmã, insere a chave e o dado
     * em alguma delas e promove o elemento que estiver sobrando de acordo com a
     * inserção feita. Caso necessário, divide a página pai também para que o
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
     * @param pilhaDeEnderecos Uma pilha com todos os endereços de todas as páginas
     * pelas quais a recursividade passou até o momento.
     * @param pilhaDeIndices Uma pilha com todos os índices dos ponteiros que a
     * recursividade tenha acessado para descer de uma página para a outra.
     */
    void inserir(TIPO_DAS_CHAVES &chave, TIPO_DOS_DADOS &dado,
        list<file_ptr_type>& pilhaDeEnderecos,
        list<int>& pilhaDeIndices)
    {
        // Após a função obterCaminhoDeDescida(), paginaFilha aponta para a última
        // página carregada na descida da árvore (alguma folha).
        int indiceDeInsercao = paginaFilha->obterIndiceDeDescida(chave);

        // Checa se a inserção teve sucesso
        if (paginaFilha->inserir(chave, dado, indiceDeInsercao))
            paginaFilha->colocarNoArquivo(arquivo); // Encerra salvando a página

        else // Inserção na página falhou, acontece quando ela está cheia.
        {
            pilhaDeEnderecos.pop_back(); // O último endereço pode ser descartado
            bool voltouParaARaiz = pilhaDeEnderecos.empty();
            int indiceDePromocao = pilhaDeIndices.back();

            pair<Pagina *, bool> infoPai(nullptr, false);
            tratarPaginaCheia(chave, dado, indiceDePromocao, infoPai);

            if (voltouParaARaiz) trocarRaizPor(paginaPai);

            else // Inicia o processo de subida na árvore
            {
                // Quando uma página que é filha da atual promove um elemento para
                // a atual ou para a irmã da atual, a variável paginaDeInsercao vai
                // ter um ponteiro para a página que receber o elemento promovido.
                Pagina *paginaDeInsercao = infoPai.first;
                bool inseriuNaPaginaAtual;
                file_ptr_type enderecoPaginaPai;

                // Checa se houve alguma promoção para a página atual
                while (paginaDeInsercao != nullptr)
                {
                    pilhaDeEnderecos.pop_back();
                    voltouParaARaiz = pilhaDeEnderecos.empty();
                    enderecoPaginaPai = voltouParaARaiz ?
                        constantes::ptrNuloPagina : pilhaDeEnderecos.back();

                    pilhaDeIndices.pop_back();
                    indiceDePromocao = pilhaDeIndices.back();
                    inseriuNaPaginaAtual = infoPai.second;

                    // As páginas que eram pais, na subida da árvore, são filhas
                    swap(paginaPai, paginaFilha);
                    swap(paginaIrmaPai, paginaIrma);

                    if (voltouParaARaiz) // Já está na raiz e precisa-se promover
                    {
                        paginaPai->limpar(); // Cria a nova raiz
                        paginaPai->ponteiros.push_back(constantes::ptrNuloPagina);
                    }

                    else carregar(paginaPai, enderecoPaginaPai);

                    promoverOParQueEstiverSobrando(
                        indiceDePromocao, paginaDeInsercao,
                        inseriuNaPaginaAtual, infoPai);

                    if (voltouParaARaiz) trocarRaizPor(paginaPai);

                    paginaDeInsercao = infoPai.first;
                }
            }
        }
    }

    /**
     * @brief Procura todos os registros que forem encontrados com a chave entre
     * as chaves informadas (Incluindo as próprias chaves). O intervalo é
     * [chaveMenor, chaveMaior].
     * 
     * <p><b>Caso o TIPO_DAS_CHAVES não seja primitivo, ele precisa interagir com os
     * operadores < (menor) e <= (menor ou igual).</b></p>
     * 
     * @see [Relational Operators Overloading](https://www.tutorialspoint.com/cplusplus/relational_operators_overloading)
     * 
     * @param chaveMenor Valor do limite inferior.
     * @param chaveMaior Valor do limite superior.
     * @param dados Referência para um vetor onde os dados relacionados às chaves
     * serão guardados.
     * @param enderecoPaginaAtual Endereço da página atual na recursividade.
     */
    void listarDadosComAChaveEntre(
        TIPO_DAS_CHAVES &chaveMenor,
        TIPO_DAS_CHAVES &chaveMaior,
        vector<TIPO_DOS_DADOS> &dados,
        file_ptr_type enderecoPaginaAtual)
    {
        if (carregar(paginaFilha, enderecoPaginaAtual))
        {
            int indiceDeDescida = paginaFilha->obterIndiceDeDescida(chaveMenor);
            int indiceFinal = 
                upper_bound(paginaFilha->chaves.begin() + indiceDeDescida,
                    paginaFilha->chaves.end(), chaveMaior)
                    - paginaFilha->chaves.begin();

            if (!paginaFilha->eUmaFolha())
            {
                listarDadosComAChaveEntre(
                    chaveMenor, chaveMaior, dados,
                    paginaFilha->ponteiros[indiceDeDescida]);

                for (int i = indiceDeDescida; i < indiceFinal; i++)
                {
                    carregar(paginaFilha, enderecoPaginaAtual);

                    dados.push_back(paginaFilha->dados[i]);
                    
                    listarDadosComAChaveEntre(
                        chaveMenor, chaveMaior, dados,
                        paginaFilha->ponteiros[i + 1]);
                }
            }

            else
            {
                dados.insert(
                    dados.end(),
                    paginaFilha->dados.begin() + indiceDeDescida,
                    paginaFilha->dados.begin() + indiceFinal);
            }
        }
    }

    /**
     * @brief Imprime, na saída padrão, uma representação vertical da árvore.
     * A saída é similar à do comando "tree /f" do Windows.
     * 
     * @param endereco Endereço de onde a página deve ser carregada do arquivo.
     * @param altura Altura atual na árvore.
     */
    void mostrar(file_ptr_type endereco, int altura)
    {
        if (carregar(paginaFilha, endereco))
        {
            string identacao(altura * 4, ' ');

            if (paginaFilha->eUmaFolha())
            {
                cout << identacao;
                paginaFilha->mostrar(cout, false, false, false, "", " ");
            }

            else
            {
                if (!paginaFilha->ponteiros.empty())
                {
                    int i = paginaFilha->ponteiros.size() - 1;
                    mostrar(paginaFilha->ponteiros[i], altura + 1);

                    for (i--; i >= 0; i--)
                    {
                        carregar(paginaFilha, endereco);

                        cout << identacao << paginaFilha->chaves[i] << endl;

                        mostrar(paginaFilha->ponteiros[i], altura + 1);
                    }
                }
            }
        }
    }

    /**
     * @brief Mostra todas as páginas da árvore recursivamente em pré-ordem.
     * 
     * <p>Caso for usar o método mostrarPre() dá classe, é necessário que a chave e
     * o dado sejam tipos primitivos ou então o operador << deve ser sobrecarregado
     * para que seja possível inserir a chave e/ou o dado num ostream (output stream)
     * como o cout.</p>
     * 
     * @see [Sobrecarregando o operador <<](https://docs.microsoft.com/pt-br/cpp/standard-library/overloading-the-output-operator-for-your-own-classes?view=vs-2019)
     * 
     * @param endereco Endereço de onde a página deve ser carregada do arquivo.
     * @param mostrarOsDados Caso seja true, mostra os dados ligados às chaves.
     * @param mostrarOsPonteiros Caso seja true, mostra os ponteiros entre os pares.
     * @param mostrarEndereco Caso seja true, mostra o endereço da página no arquivo.
     * @param delimitadorEntreOPonteiroEAChave Delimitador entre um ponteiro e uma chave.
     * @param delimitadorEntreODadoEOPonteiro Delimitador entre um dado e um ponteiro.
     * @param delimitadorEntreAChaveEODado Delimitador entre uma chave e um dado.
     */
    void mostrarPre(
        file_ptr_type endereco,
        bool mostrarOsDados = false,
        bool mostrarOsPonteiros = true,
        bool mostrarEndereco = true,
        string delimitadorEntreOPonteiroEAChave = " (",
        string delimitadorEntreODadoEOPonteiro = ") ",
        string delimitadorEntreAChaveEODado = ", ")
    {
        if (carregar(paginaFilha, endereco))
        {
            cout << endl;
            paginaFilha->mostrar(
                cout, mostrarOsDados, mostrarOsPonteiros, mostrarEndereco,
                delimitadorEntreOPonteiroEAChave,
                delimitadorEntreODadoEOPonteiro,
                delimitadorEntreAChaveEODado);

            if (!paginaFilha->eUmaFolha())
            {
                auto ponteiros = paginaFilha->ponteiros;

                for (auto &&i : ponteiros)
                {
                    if (i != constantes::ptrNuloPagina)
                    {
                        mostrarPre(
                            i, mostrarOsDados, mostrarOsPonteiros, mostrarEndereco,
                            delimitadorEntreOPonteiroEAChave,
                            delimitadorEntreODadoEOPonteiro,
                            delimitadorEntreAChaveEODado);
                    }
                }
            }
        }
    }

    file_ptr_type lerEnderecoDaRaiz()
    {
        // Pula as coisas do cabeçalho do arquivo que vierem antes do endereço da raiz
        arquivo.seekg(tamanhoCabecalhoAntesDoEnderecoDaRaiz);

        file_ptr_type endereco;
        arquivo >> endereco; // Carrega o endereço da raiz

        if (arquivo.fail())
        {
            // cerr é a saída padrão de erros. Em alguns caso pode ser igual a cout.
            cerr << "[ArvoreB] Não foi possível ler o endereço da raiz do arquivo."
                 << endl
                 << "Exceção lançada" << endl;

            throw length_error(
                "[ArvoreB] Não foi possível ler o endereço da raiz do arquivo.");
        }

        return endereco;
    }

public:
    // ------------------------- Construtores e destrutores

    ArvoreB(string nomeDoArquivo, int ordemDaArvore) :
        nomeDoArquivo(nomeDoArquivo),
        numeroDeChavesPorPagina(ordemDaArvore - 1),
        ordemDaArvore(ordemDaArvore),
        paginaPai( new Pagina(ordemDaArvore) ),
        paginaIrmaPai( new Pagina(ordemDaArvore) ),
        paginaFilha( new Pagina(ordemDaArvore) ),
        paginaIrma( new Pagina(ordemDaArvore) )
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
     * @brief Procura o primeiro registro com a chave informada e pega o dado
     * correspondente a ela.
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
     * Em casos onde a chave não é encontrada, uma flag interna é ativada. Dessa
     * forma, você pode usar qualquer um dos dois ifs abaixo para checar erros:
     * 
     * @code{.cpp}
     * if (ArvoreB.pesquisar(chave) == TIPO_DOS_DADOS()) ArvoreB.mostrarErro();
     * if (ArvoreB.erro()) ArvoreB.mostrarErro();
     * @endcode
     */
    TIPO_DOS_DADOS pesquisar(TIPO_DAS_CHAVES& chave)
    {
        TIPO_DOS_DADOS dado;

        // Faz todo o percurso de descida na árvore
        obterCaminhoDeDescida(chave, 0, lerEnderecoDaRaiz());

        // Obtém o índice onde a chave deveria estar na página.
        // paginaFilha é a última página do percurso.
        int indiceDaChave = paginaFilha->obterIndiceDeDescida(chave);

        if (indiceDaChave == paginaFilha->tamanho() ||
            paginaFilha->chaves[indiceDaChave] != chave)
        {
            atribuirErro("A chave não foi encontrada");
        }

        else
        {
            limparErro();
            dado = paginaFilha->dados[indiceDaChave];
        }

        return dado;
    }

    TIPO_DOS_DADOS pesquisar(TIPO_DAS_CHAVES&& chave)
    {
        return pesquisar(chave);
    }

    /**
     * @brief Procura todos os registros que forem encontrados com a chave entre
     * as chaves informadas. Inclui as próprias chaves. O intervalo é
     * [chaveMenor, chaveMaior].
     * 
     * <p><b>Caso o TIPO_DAS_CHAVES não seja primitivo, ele precisa interagir com os
     * operadores < (menor) e <= (menor ou igual).</b></p>
     * 
     * @see [Relational Operators Overloading](https://www.tutorialspoint.com/cplusplus/relational_operators_overloading)
     * 
     * @param chaveMenor Valor do limite inferior.
     * @param chaveMaior Valor do limite superior.
     * 
     * @return vector<TIPO_DOS_DADOS> Vetor com cada dado correspondente à chave.
     */
    vector<TIPO_DOS_DADOS> listarDadosComAChaveEntre(
        TIPO_DAS_CHAVES& chaveMenor,
        TIPO_DAS_CHAVES& chaveMaior)
    {
        vector<TIPO_DOS_DADOS> dados;

        if (chaveMenor <= chaveMaior)
        {
            listarDadosComAChaveEntre(
                chaveMenor, chaveMaior, dados, lerEnderecoDaRaiz());
        }

        return dados;
    }

    vector<TIPO_DOS_DADOS> listarDadosComAChaveEntre(
        TIPO_DAS_CHAVES&& chaveMenor,
        TIPO_DAS_CHAVES&& chaveMaior)
    {
        return listarDadosComAChaveEntre(chaveMenor, chaveMaior);
    }

    /**
     * @brief Procura todos os registros que forem encontrados com a chave informada.
     * 
     * @param chave Chave a ser procurada.
     * 
     * @return vector<TIPO_DOS_DADOS> Vetor com cada dado correspondente à chave.
     */
    vector<TIPO_DOS_DADOS> listarDadosComAChave(TIPO_DAS_CHAVES& chave)
    {
        return listarDadosComAChaveEntre(chave, chave);
    }

    vector<TIPO_DOS_DADOS> listarDadosComAChave(TIPO_DAS_CHAVES&& chave)
    {
        return listarDadosComAChave(chave);
    }

    /**
     * @brief Insere o par (chave, dado) na árvore.
     * 
     * @param chave Chave a ser inserida.
     * @param dado Dado a ser inserido.
     */
    void inserir(TIPO_DAS_CHAVES& chave, TIPO_DOS_DADOS& dado)
    {
        // Faz todo o percurso de descida na árvore
        auto parDoCaminho =
            obterCaminhoDeDescida(chave, 0, lerEnderecoDaRaiz(), true);

        auto& pilhaDeEnderecos = parDoCaminho.first;
        auto& pilhaDeIndices = parDoCaminho.second;

        inserir(chave, dado, pilhaDeEnderecos, pilhaDeIndices);
    }

    /**
     * @brief Insere o par (chave, dado) na árvore.
     * 
     * @param chave Chave a ser inserida.
     * @param dado Dado a ser inserido.
     */
    void inserir(TIPO_DAS_CHAVES&& chave, TIPO_DOS_DADOS&& dado)
    {
        inserir(chave, dado);
    }

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
     * Em casos onde a chave não é encontrada, uma flag interna é ativada. Dessa
     * forma, você pode usar qualquer um dos dois ifs abaixo para checar erros:
     * 
     * @code{.cpp}
     * if (ArvoreB.excluir(chave) == TIPO_DOS_DADOS()) ArvoreB.mostrarErro();
     * if (ArvoreB.erro()) ArvoreB.mostrarErro();
     * @endcode
     */
    TIPO_DOS_DADOS excluir(TIPO_DAS_CHAVES& chave)
    {
        // Faz todo o percurso de descida na árvore
        auto parDoCaminho = obterCaminhoDeDescida(chave, 0, lerEnderecoDaRaiz());
        auto& pilhaDeEnderecos = parDoCaminho.first;
        auto& pilhaDeIndices = parDoCaminho.second;

        return excluir(chave, pilhaDeEnderecos, pilhaDeIndices);
    }

    TIPO_DOS_DADOS excluir(TIPO_DAS_CHAVES&& chave)
    {
        return excluir(chave);
    }

    /**
     * @brief Imprime, na saída padrão, uma representação vertical da árvore.
     * A saída é similar à do comando "tree /f" do Windows.
     */
    void mostrar()
    {
        mostrar(lerEnderecoDaRaiz(), 0);
    }

    /**
     * @brief Mostra todas as páginas da árvore recursivamente em pré-ordem.
     * 
     * <p>Caso for usar o método mostrarPre() dá classe, é necessário que a chave e
     * o dado sejam tipos primitivos ou então o operador << deve ser sobrecarregado
     * para que seja possível inserir a chave e/ou o dado num ostream (output stream)
     * como o cout.</p>
     * 
     * @see [Sobrecarregando o operador <<](https://docs.microsoft.com/pt-br/cpp/standard-library/overloading-the-output-operator-for-your-own-classes?view=vs-2019)
     * 
     * @param mostrarOsDados Caso seja true, mostra os dados ligados às chaves.
     * @param mostrarOsPonteiros Caso seja true, mostra os ponteiros entre os pares.
     * @param mostrarEndereco Caso seja true, mostra o endereço da página no arquivo.
     * @param delimitadorEntreOPonteiroEAChave Delimitador entre um ponteiro e uma chave.
     * @param delimitadorEntreODadoEOPonteiro Delimitador entre um dado e um ponteiro.
     * @param delimitadorEntreAChaveEODado Delimitador entre uma chave e um dado.
     */
    void mostrarPre(
        bool mostrarOsDados = false,
        bool mostrarOsPonteiros = true,
        bool mostrarEndereco = true,
        string delimitadorEntreOPonteiroEAChave = " (",
        string delimitadorEntreODadoEOPonteiro = ") ",
        string delimitadorEntreAChaveEODado = ", ")
    {
        cout << "Raiz:";
        mostrarPre(
            lerEnderecoDaRaiz(),
            mostrarOsDados, mostrarOsPonteiros, mostrarEndereco,
            delimitadorEntreOPonteiroEAChave,
            delimitadorEntreODadoEOPonteiro,
            delimitadorEntreAChaveEODado);
    }
};

// 364 linhas gastas em documentação
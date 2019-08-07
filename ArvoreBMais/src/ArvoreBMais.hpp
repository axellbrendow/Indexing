/**
 * @file ArvoreBMais.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo da classe ArvoreBMais.
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
#include "ArvoreB.hpp"
#include "PaginaBMais.hpp"

#include <iostream>
#include <fstream>
#include <list>

using namespace std;

/**
 * @brief Classe da árvore B+, uma estrutura eficiente para indexamento de registros
 * em disco.
 * 
 * @tparam TIPO_DAS_CHAVES Tipo da chave dos registros. <b>É necessário que a chave
 * seja um tipo primitivo ou então que a sua classe/struct herde de Serializavel e
 * tenha um construtor sem parâmetros.</b>
 * @tparam TIPO_DOS_DADOS Tipo do dado dos registros. <b>É necessário que o dado
 * seja um tipo primitivo ou então que a sua classe/struct herde de Serializavel e
 * tenha um construtor sem parâmetros.</b>
 * @tparam Pagina Tipo das páginas da árvore B+. <b>É necessário que esse tipo seja
 * serializável.</b>
 */
template<
    typename TIPO_DAS_CHAVES,
    typename TIPO_DOS_DADOS,
    typename Pagina = PaginaBMais<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> >
class ArvoreBMais : public ArvoreB< TIPO_DAS_CHAVES, TIPO_DOS_DADOS, Pagina >
{
public:
    // ------------------------- Typedefs

    typedef ArvoreB< TIPO_DAS_CHAVES, TIPO_DOS_DADOS, Pagina > ArvoreBHerdada;

protected:
    // ------------------------- Campos e métodos herdados
    // Com o using, esses campos da árvore B herdada ficam diretamente
    // acessíveis nesta classe

    using ArvoreBHerdada::arquivo;
    using ArvoreBHerdada::atribuirErro;
    using ArvoreBHerdada::carregar;
    using ArvoreBHerdada::lerEnderecoDaRaiz;
    using ArvoreBHerdada::limparErro;
    using ArvoreBHerdada::obterCaminhoDeDescida;
    using ArvoreBHerdada::obterPaginaDeInsercao;
    using ArvoreBHerdada::ordemDaArvore;

public:
    // ------------------------- Campos e métodos herdados
    // Com o using, esses campos da árvore B herdada ficam diretamente
    // acessíveis nesta classe

    using ArvoreBHerdada::excluir;
    using ArvoreBHerdada::listarDadosComAChaveEntre;
    using ArvoreBHerdada::paginaFilha;
    using ArvoreBHerdada::paginaIrma;
    using ArvoreBHerdada::paginaIrmaPai;
    using ArvoreBHerdada::paginaPai;
    using ArvoreBHerdada::pesquisar;

    // ------------------------- Construtores e destrutores

    ArvoreBMais(string nomeDoArquivo, int ordemDaArvore) : ArvoreBHerdada(nomeDoArquivo, ordemDaArvore) {}

    // ------------------------- Métodos

    /**
     * @brief Faz com que a próxima página da irma seja a página para a qual
     * a filha está apontando, depois, faz com que a filha aponte para a irma.
     * 
     * @param filha Página à esquerda na divisão.
     * @param irma Página à direita na divisão.
     */
    void atualizarAposADivisao(Pagina *filha, Pagina *irma)
    {
        irma->setEndereco( obterTamanhoEmBytes(arquivo) );
        irma->ptrProximaPagina = filha->ptrProximaPagina;
        filha->ptrProximaPagina = irma->obterEndereco();
    }

    pair<Pagina *, bool> dividir(Pagina *filha, Pagina *irma, TIPO_DAS_CHAVES &chave)
    {
        auto par = ArvoreBHerdada::dividir(filha, irma, chave);

        atualizarAposADivisao(filha, irma);

        return par;
    }

    void promoverOParQueEstiverSobrando(
        int indiceDePromocao, Pagina *paginaDeInsercao,
        bool inseriuNaPaginaFilha, pair<Pagina *, bool> &infoPai)
    {
        ArvoreBHerdada::promoverOParQueEstiverSobrando(
            indiceDePromocao, paginaDeInsercao,
            inseriuNaPaginaFilha, infoPai);

        // Checa se a paginaPai teve que ser dividida antes da promoção
        if (infoPai.first != nullptr)
        {
            atualizarAposADivisao(paginaPai, paginaIrmaPai);
        }
    }

    bool fundirCom(
        file_ptr_type enderecoDaPagina, int indiceDeDescida, bool fundirDireita) override
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
                paginaFilha->ptrProximaPagina = paginaIrma->ptrProximaPagina;
            }

            else
            {
                paginaPai->transferirElementoPara(
                    paginaIrma, paginaIrma->tamanho(), indiceDeDescida - 1,
                    false, true, false);
                    
                paginaFilha->transferirTudoPara(paginaIrma);
                paginaIrma->ptrProximaPagina = paginaFilha->ptrProximaPagina;
            }

            paginaPai->colocarNoArquivo(arquivo);
            paginaIrma->colocarNoArquivo(arquivo);

            sucesso = true;
        }
        
        return sucesso;
    }

    TIPO_DOS_DADOS pesquisar(TIPO_DAS_CHAVES &chave) override
    {
        TIPO_DOS_DADOS dado;

        // Faz todo o percurso de descida na árvore
        obterCaminhoDeDescida(chave, 0, lerEnderecoDaRaiz(), true);

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

    TIPO_DOS_DADOS excluir(TIPO_DAS_CHAVES &chave) override
    {
        // Faz todo o percurso de descida na árvore
        auto parDoCaminho = obterCaminhoDeDescida(chave, 0, lerEnderecoDaRaiz(), true);
        auto &pilhaDeEnderecos = parDoCaminho.first;
        auto &pilhaDeIndices = parDoCaminho.second;

        return excluir(chave, pilhaDeEnderecos, pilhaDeIndices);
    }

    int obterDadosComAChaveEntre(
        TIPO_DAS_CHAVES &chaveMenor,
        TIPO_DAS_CHAVES &chaveMaior,
        vector<TIPO_DOS_DADOS> &dados)
    {
        // Obtém o índice onde a chave deveria estar na página.
        int indiceDaChave = paginaFilha->obterIndiceDeDescida(chaveMenor);
        int indiceFinal = 
            upper_bound(paginaFilha->chaves.begin() + indiceDaChave,
                paginaFilha->chaves.end(), chaveMaior)
                - paginaFilha->chaves.begin();

        for (size_t i = indiceDaChave; i < indiceFinal; i++)
        {
            dados.push_back(paginaFilha->dados[i]);
        }

        return indiceFinal;
    }

    vector<TIPO_DOS_DADOS> listarDadosComAChaveEntre(
        TIPO_DAS_CHAVES &chaveMenor,
        TIPO_DAS_CHAVES &chaveMaior) override
    {
        vector<TIPO_DOS_DADOS> dados;

        if (chaveMenor <= chaveMaior)
        {
            obterCaminhoDeDescida(chaveMenor, 0, lerEnderecoDaRaiz(), true);
            int indiceFinal = obterDadosComAChaveEntre(chaveMenor, chaveMaior, dados);
            
            while (indiceFinal == paginaFilha->tamanho() &&
                paginaFilha->ptrProximaPagina != constantes::ptrNuloPagina)
            {
                carregar(paginaFilha, paginaFilha->ptrProximaPagina);
                indiceFinal = obterDadosComAChaveEntre(chaveMenor, chaveMaior, dados);
            }
        }

        return dados;
    }
};

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
class ArvoreBMais : public ArvoreB< TIPO_DAS_CHAVES, TIPO_DOS_DADOS, PaginaBMais<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> >
{
public:
    // ------------------------- Typedefs

    /**
     * @brief Padroniza o tipo da página da árvore. Typedefs dentro de classes ou
     * structs são considerados como boa prática em C++.
     */
    typedef ArvoreB< TIPO_DAS_CHAVES, TIPO_DOS_DADOS, PaginaBMais<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> > ArvoreBHerdada;
    typedef PaginaBMais<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> Pagina;

protected:
    // ------------------------- Campos e métodos herdados

    using ArvoreBHerdada::obterTamanhoDoArquivo;
    using ArvoreBHerdada::paginaPai;
    using ArvoreBHerdada::paginaIrmaPai;
    using ArvoreBHerdada::paginaFilha;
    using ArvoreBHerdada::paginaIrma;
    using ArvoreBHerdada::arquivo;
    using ArvoreBHerdada::carregar;
    using ArvoreBHerdada::obterPaginaDeInsercao;

public:
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
        irma->setEndereco( obterTamanhoDoArquivo() );
        irma->ptrProximaPagina = filha->ptrProximaPagina;
        filha->ptrProximaPagina = irma->obterEndereco();
    }

    virtual pair<Pagina *, bool> dividir(Pagina *filha, Pagina *irma, TIPO_DAS_CHAVES &chave)
    {
        auto par = ArvoreBHerdada::dividir(filha, irma, chave);

        atualizarAposADivisao(filha, irma);

        return par;
    }

    virtual void promoverOParQueEstiverSobrando(
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
    virtual bool fundirCom(
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
};

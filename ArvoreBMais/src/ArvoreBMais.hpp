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
    typedef ArvoreB< TIPO_DAS_CHAVES, TIPO_DOS_DADOS, PaginaBMais<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> > MyArvoreB;

    // ------------------------- Construtores e destrutores

    ArvoreBMais(string nomeDoArquivo, int ordemDaArvore) : MyArvoreB(nomeDoArquivo, ordemDaArvore) {}
};

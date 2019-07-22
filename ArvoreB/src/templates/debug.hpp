/**
 * @file debug.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo com helpers para debug.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#pragma once

#include "tipos.hpp"

#define DEBUG cout << endl << "#######################################" << endl;
#define DEBUGV(value) cout << "debug = " << value << endl;

template<typename tipo>
void debugIterar(tipo start, tipo end)
{
    cout << (int) *start;
    
    if (start != end)
    {
        for (tipo i = start + 1; i != end; i++)
        {
            cout << " " << (int) *i;
        }
    }
    
    cout << endl;
}

template<typename tipo>
void debugPrintar(tipo value)
{
    cout << value << endl;

    DEBUG
}

template<typename tipo, typename... OutrosTipos>
void debugPrintar(tipo value, OutrosTipos... outrosTipos)
{
    DEBUG

    string delimiter = " ";
    cout << value << delimiter;
    debugPrintar(outrosTipos...);

    DEBUG
}

template<typename tipo>
void debugCursorComSaida(iterador start, iterador end, tipo *output)
{
    for (iterador i = start; i != end; i++)
    {
        cout << "(input) " << (int) *i << ",";
        *output = *i;
        cout << "(output) " << (int) *output++ << ",";
    } cout << endl;
}
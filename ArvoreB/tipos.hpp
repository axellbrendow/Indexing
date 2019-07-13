/**
 * @file tipos.hpp
 * @author Axell Brendow ( breno.axel@gmail.com ) ( https://github.com/axell-brendow )
 * @brief Classe de intermediação entre a entrada de dados e a sua variável.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#pragma once

#include <vector>

using namespace std;

/**
 * Links úteis doxygen:<br>
 * 
 * https://flcwiki.desy.de/How%20to%20document%20your%20code%20using%20doxygen#How_to_create_a_configuration_file<br>
 * https://gist.github.com/ugovaretto/261bd1d16d9a0d2e76ee<br>
 */

/**
 * Links úteis smart pointers:<br>
 * 
 * https://docs.microsoft.com/pt-br/cpp/cpp/smart-pointers-modern-cpp?view=vs-2019<br>
 * https://docs.microsoft.com/pt-br/cpp/cpp/how-to-create-and-use-shared-ptr-instances?view=vs-2019<br>
 */

/**
 * Em C++, usamos unsigned char para lidar com bytes, afinal o intervalo de valores do tipo unsigned char
 * é o mesmo do intervalo de um byte, 0 a 255.
 */
typedef unsigned char tipo_byte;

/**
 * Tipo destinado a ser usado para trabalhar com vetores de bytes.
 */
typedef vector<tipo_byte> vetor_de_bytes;
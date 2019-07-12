#include <vector>

using namespace std;

/**
 * Links úteis doxygen:<br>
 * 
 * https://flcwiki.desy.de/How%20to%20document%20your%20code%20using%20doxygen#How_to_create_a_configuration_file<br>
 * https://gist.github.com/ugovaretto/261bd1d16d9a0d2e76ee<br>
 */

/**
 * Em C++, usamos unsigned char para lidar com bytes, afinal o range do tipo unsigned char é entre 0 e 255.
 */
typedef unsigned char tipo_byte;

/**
 * Oh my gudness
 */
typedef vector<tipo_byte> vetor_de_bytes;
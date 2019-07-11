/**
 * @file PaginaB.cpp
 * @author Axell Brendow (breno.axel@gmail.com)
 * @brief Arquivo para testes
 * @version 0.1
 * @date 2019-07-11
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 * 
 */

#include <iostream>

using namespace std;

typedef unsigned char byte;

class PaginaB
{
    private:
        byte *byteArray; // guardará os dados da página que vierem do disco
        int numeroDeChaves;

    public:
        PaginaB(byte *byteArray)
        {
            //
        }
};

ostream& operator<< (ostream &stream, PaginaB &pagina)
{
    return stream << pagina;
}

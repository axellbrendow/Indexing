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

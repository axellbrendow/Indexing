/**
 * @file PaginaB.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo para testes
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#include "tipos.hpp"
#include "templates/serializavel.hpp"

#include <iostream>

using namespace std;

template<typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
class PaginaB : public Serializavel
{
    private:
        /** Guardará os dados da página que vierem do arquivo. */
        vetor_de_bytes bytes;
        int numeroDeElementos;
        vector<TIPO_DAS_CHAVES> chaves;
        vector<TIPO_DOS_DADOS> dados;
        vector<file_pointer_type> ponteiros;

        PaginaB(int maximoDeBytesParaAChave,
            int maximoDeBytesParaODado,
            int numeroDeChavesPorPagina,
            int ordemDaArvore) :
            maximoDeBytesParaAChave(maximoDeBytesParaAChave),
            maximoDeBytesParaODado(maximoDeBytesParaODado),
            numeroDeChavesPorPagina(numeroDeChavesPorPagina),
            ordemDaArvore(ordemDaArvore) {}

    public:
        const int maximoDeBytesParaAChave;
        const int maximoDeBytesParaODado;
        const int numeroDeChavesPorPagina;
        const int ordemDaArvore;

        PaginaB(vetor_de_bytes bytes) : bytes(bytes) {}

        int obterTamanhoMaximoEmBytes()
        {

        }
};

template<typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
ostream& operator<< (ostream &stream, PaginaB<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> &pagina)
{
    return stream << pagina;
}

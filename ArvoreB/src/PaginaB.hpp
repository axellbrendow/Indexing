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
        // ------------------------- Campos

        /** Guardará os dados da página que vierem do arquivo. */
        DataOutputStream bytes;
        int numeroDeElementos;
        vector<TIPO_DAS_CHAVES> chaves;
        vector<TIPO_DOS_DADOS> dados;
        vector<file_pointer_type> ponteiros;

        // ------------------------- Construtores

        PaginaB(int maximoDeBytesParaAChave,
            int maximoDeBytesParaODado,
            int numeroDeChavesPorPagina,
            int ordemDaArvore,
            int numeroDeElementos) :

            maximoDeBytesParaAChave(maximoDeBytesParaAChave),
            maximoDeBytesParaODado(maximoDeBytesParaODado),
            numeroDeChavesPorPagina(numeroDeChavesPorPagina),
            ordemDaArvore(ordemDaArvore),
            numeroDeElementos(numeroDeElementos) {}

    public:
        // ------------------------- Campos

        const int maximoDeBytesParaAChave;
        const int maximoDeBytesParaODado;
        const int numeroDeChavesPorPagina;
        const int ordemDaArvore;

        // ------------------------- Construtores

        PaginaB(int maximoDeBytesParaAChave,
            int maximoDeBytesParaODado,
            int ordemDaArvore) :

            PaginaB(maximoDeBytesParaAChave,
                maximoDeBytesParaODado,
                ordemDaArvore - 1,
                ordemDaArvore, 0) {}

        PaginaB(vetor_de_bytes bytes) : bytes(bytes) {}

        // ------------------------- Métodos

        int obterTamanhoMaximoEmBytes()
        {
            return sizeof(int) + // bytes para guardar a quantidade de elementos na página
                ordemDaArvore * sizeof(file_pointer_type) + // bytes para os ponteiros
                numeroDeChavesPorPagina * maximoDeBytesParaAChave + // bytes para as chaves
                numeroDeChavesPorPagina * maximoDeBytesParaODado; // bytes para os dados
        }

        DataOutputStream gerarDataOutputStream(DataOutputStream out)
        {
			Serializavel chave;
			Serializavel dado;

            out << numeroDeElementos << ponteiros.at(0);

			for (size_t i = 0; i < numeroDeChavesPorPagina; i++)
			{
				chave = chaves.at(i);
				dado = dados.at(i);

				out << chave;
				out << dado;
				out << ponteiros.at(i + 1);
			}
        }

        void lerBytes(DataInputStream input)
        {
            input >> numeroDeElementos;
        }
};

template<typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
ostream& operator<< (ostream &stream, PaginaB<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> &pagina)
{
    return stream << pagina;
}

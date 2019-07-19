/**
 * @file PaginaB.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo para testes
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#include "templates/tipos.hpp"
#include "templates/serializavel.hpp"

#include <iostream>

using namespace std;

template<typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
class PaginaB : public Serializavel
{
private:
    // ------------------------- Campos

    /** Guardará os dados da página que vierem do arquivo. */
    DataInputStream bytes;
    int numeroDeElementos;

public:
    // ------------------------- Campos
    
    const vector<TIPO_DAS_CHAVES> chaves;
    const vector<TIPO_DOS_DADOS> dados;
    const vector<file_pointer_type> ponteiros;

    // ------------------------- Construtores

    /**
     * @brief Constrói uma nova página vazia.
     */
    PaginaB() {}

    /**
     * @brief Constrói uma nova página a partir do vetor de bytes do DataInputStream.
     * 
     * @param bytes DataInputStream com o vetor de bytes da página.
     */
    PaginaB(DataInputStream &bytes) : bytes(bytes) {}

    // ------------------------- Métodos herdados de Serializavel

    virtual int obterTamanhoMaximoEmBytes() override
    {
        return sizeof(int) + // bytes para guardar a quantidade de elementos na página
            ordemDaArvore * sizeof(file_pointer_type) + // bytes para os ponteiros
            numeroDeChavesPorPagina * maximoDeBytesParaAChave + // bytes para as chaves
            numeroDeChavesPorPagina * maximoDeBytesParaODado; // bytes para os dados
    }

    virtual DataOutputStream gerarDataOutputStream(DataOutputStream out) override
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

    virtual void lerBytes(DataInputStream &input) override
    {
        input >> numeroDeElementos;
    }

    // ------------------------- Métodos

    /**
     * @brief Retorna o número de elementos da página caso ela tenha sido carregada.
     * 
     * @return int Retorna o número de elementos da página.
     */
    int obterNumeroDeElementos()
    {
        return numeroDeElementos;
    }
};

template<typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
ostream& operator<< (ostream &stream, PaginaB<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> &pagina)
{
    return stream << pagina;
}

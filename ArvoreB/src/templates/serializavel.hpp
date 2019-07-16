/**
 * @file serializavel.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo destinado a guardar classes úteis para a serialização de objetos.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#include "tipos.hpp"
#include "../streams/DataOutputStream.hpp"
#include "../streams/DataInputStream.hpp"

#include <fstream>

static str_size_type padraoTamanhoMaximoStrings = 300;

/**
 * @brief Classe abstrata que todo objeto serializável deve herdar.
 * 
 * @see [Interfaces C++](https://www.tutorialspoint.com/cplusplus/cpp_interfaces)
 * @see [Serialização - Wikipédia](https://pt.wikipedia.org/wiki/Serializa%C3%A7%C3%A3o)
 * @see [Funções virtuais](https://techdifferences.com/difference-between-virtual-function-and-pure-virtual-function.html)
 */
class Serializavel
{
    public:
        // ------------------------- Métodos

        /**
         * @brief Calcula o tamanho máximo, em bytes, que a entidade pode gastar.
         * 
         * @return int Retorna o tamanho máximo, em bytes, que a entidade pode gastar.
         */
        virtual int obterTamanhoMaximoEmBytes() = 0; // = 0 declara esta função como pura
        
        /**
         * @brief Insere os dados da entidade no DataOutputStream recebido.
         * 
         * @param out Um objeto DataOutputStream com espaço alocado para o tamanho máximo da
         * entidade.
         * 
         * @return DataOutputStream Retorna o DataOutputStream contendo o vetor de bytes com os
         * dados da entidade.
         */
        virtual DataOutputStream gerarDataOutputStream(DataOutputStream out) = 0;

        /**
         * @brief Lê e interpreta o vetor do input restaurando o objeto da entidade.
         * 
         * @param input DataInputStream com o vetor de bytes da entidade.
         */
        virtual void lerBytes(DataInputStream input) = 0; // = 0 declara esta função como pura

        /**
         * @brief Lê e interpreta o buffer restaurando o objeto da entidade.
         * 
         * @param buffer Vetor de bytes da entidade.
         * @param tamanho Quantidade de bytes no buffer.
         */
        void lerBytes(char *buffer, int tamanho)
        {
            DataInputStream input(buffer, tamanho);
            
            lerBytes(input);
        }

        /**
         * @brief Cria o DataOutputStream alocando obterTamanhoMaximoEmBytes() para o seu vetor.
         * 
         * @return DataOutputStream Retorna o DataOutputStream com obterTamanhoMaximoEmBytes() de
         * espaço alocado.
         */
        DataOutputStream alocarDataOutputStream()
        {
            return DataOutputStream( obterTamanhoMaximoEmBytes() );
        }
        
        /**
         * @brief Gera o DataOutputStream com os dados da entidade.
         * 
         * @return DataOutputStream Retorna o DataOutputStream contendo o vetor de bytes com os
         * dados da entidade.
         */
        DataOutputStream gerarDataOutputStream()
        {
            return gerarDataOutputStream( alocarDataOutputStream() );
        }

        /**
         * @brief Cria o DataInputStream alocando obterTamanhoMaximoEmBytes() para o seu vetor.
         * 
         * @return DataInputStream Retorna o DataInputStream com obterTamanhoMaximoEmBytes() de
         * espaço alocado.
         */
        DataInputStream alocarDataInputStream()
        {
            return DataInputStream( obterTamanhoMaximoEmBytes() );
        }

        /**
         * @brief Gera o vetor de bytes da entidade.
         * 
         * @return vetor_de_bytes Retorna o vetor de bytes da entidade.
         */
        vetor_de_bytes gerarBytes()
        {
            return gerarDataOutputStream().obterVetor();
        }
};

ostream &operator<<(ostream &ostream, Serializavel &serializavel)
{
    auto out = serializavel.gerarDataOutputStream();

    return ostream << out;
}

ofstream &operator<<(ofstream &ofstream, Serializavel &serializavel)
{
    auto out = serializavel.gerarDataOutputStream();
    
    return ofstream << out;
}

ifstream &operator>>(ifstream &ifstream, Serializavel &serializavel)
{
    auto quantidadeDeBytes = serializavel.obterTamanhoMaximoEmBytes();

    char buffer[quantidadeDeBytes]; // Cria um buffer temporário para ler do arquivo
    
    // Lê a quantidade máxima de bytes que a entidade pode gastar para o buffer
    ifstream.read(buffer, quantidadeDeBytes);
    
    serializavel.lerBytes(buffer, quantidadeDeBytes); // Interpreta os bytes e restaura o objeto da entidade

    return ifstream;
}
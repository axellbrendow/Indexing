/**
 * @file serializavel.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo destinado a guardar classes úteis para a serialização de objetos.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#pragma once

#include "tipos.hpp"
#include "debug.hpp"
#include "../streams/DataOutputStream.hpp"
#include "../streams/DataInputStream.hpp"

#include <fstream>

namespace constantes
{
    static str_size_type tamanhoMaximoStrings = 300;
}

/**
 * @brief Classe abstrata que todo objeto serializável deve herdar.
 * 
 * @see [Interfaces C++](https://www.tutorialspoint.com/cplusplus/cpp_interfaces)
 * @see [Serialização - Wikipédia](https://pt.wikipedia.org/wiki/Serializa%C3%A7%C3%A3o)
 */
class Serializavel
{
public:
    // ------------------------- Métodos

    /**
     * @brief Calcula o tamanho máximo, em bytes, que a entidade pode gastar.
     * 
     * @return int Retorna o tamanho máximo, em bytes, que a entidade pode gastar.
     * 
     * @see [Funções virtuais](https://www.tutorialspoint.com/difference-between-a-virtual-function-and-a-pure-virtual-function-in-cplusplus)
     */
    virtual int obterTamanhoMaximoEmBytes() = 0; // = 0 declara esta função como pura
    
    /**
     * @brief Insere os dados da entidade no DataOutputStream recebido e o retorna.
     * 
     * @param out Um objeto DataOutputStream com espaço alocado para o tamanho máximo da
     * entidade.
     * 
     * @return DataOutputStream Retorna o DataOutputStream contendo o vetor de bytes com os
     * dados da entidade.
     * 
     * @see [Funções virtuais](https://www.tutorialspoint.com/difference-between-a-virtual-function-and-a-pure-virtual-function-in-cplusplus)
     */
    virtual DataOutputStream& gerarDataOutputStream(DataOutputStream& out) = 0;

    /**
     * @brief Lê e interpreta o vetor do input restaurando o objeto da entidade.
     * 
     * @param input DataInputStream com o vetor de bytes da entidade.
     * 
     * @see [Funções virtuais](https://www.tutorialspoint.com/difference-between-a-virtual-function-and-a-pure-virtual-function-in-cplusplus)
     */
    virtual void lerBytes(DataInputStream& input) = 0; // = 0 declara esta função como pura

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
     * @brief Gera o DataOutputStream com os dados da entidade. O vetor interno do
     * DataOutputStream sempre terá um tamanho igual a obterTamanhoMaximoEmBytes().
     * 
     * @return DataOutputStream Retorna o DataOutputStream contendo o vetor de bytes
     * com os dados da entidade.
     */
    DataOutputStream gerarDataOutputStream()
    {
        DataOutputStream out = alocarDataOutputStream();

        return gerarDataOutputStream(out).resize( obterTamanhoMaximoEmBytes() );
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

DataOutputStream& operator<<(DataOutputStream& dataOutputStream, Serializavel* variavel)
{
    auto out = variavel->gerarDataOutputStream();

    return dataOutputStream << out;
}

DataOutputStream& operator<<(DataOutputStream& dataOutputStream, Serializavel& variavel)
{
    return dataOutputStream << &variavel;
}

ostream& operator<<(ostream& ostream, Serializavel* serializavel)
{
    auto out = serializavel->gerarDataOutputStream();

    return ostream << out;
}

ostream& operator<<(ostream& ostream, Serializavel& serializavel)
{
    return ostream << &serializavel;
}

fstream& operator<<(fstream& fstream, Serializavel* serializavel)
{
    auto out = serializavel->gerarDataOutputStream();
    
    return fstream << out;
}

fstream& operator<<(fstream& fstream, Serializavel& serializavel)
{
    return fstream << &serializavel;
}

fstream& operator>>(fstream& fstream, Serializavel* serializavel)
{
    auto quantidadeDeBytes = serializavel->obterTamanhoMaximoEmBytes();
    
    char buffer[quantidadeDeBytes]; // Cria um buffer temporário para ler do arquivo
    
    // Lê a quantidade máxima de bytes que a entidade pode gastar para o buffer
    fstream.read(buffer, quantidadeDeBytes);

    if (fstream.fail())
    {
        // cerr é a saída padrão de erros. Em alguns caso pode ser igual a cout.
        cerr << "[Serializavel] Não foi possível ler a entidade do arquivo."
            << endl << "Exceção lançada" << endl;

        throw length_error("[Serializavel] Não foi possível ler a entidade do arquivo.");
    }
    
    // Interpreta os bytes e restaura o objeto da entidade
    serializavel->lerBytes(buffer, quantidadeDeBytes);
    
    return fstream;
}

fstream& operator>>(fstream& fstream, Serializavel& serializavel)
{
    return fstream >> &serializavel;
}
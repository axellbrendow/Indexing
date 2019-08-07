/**
 * @file PaginaBMais.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo da classe PaginaBMais.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#include "templates/tipos.hpp"
#include "templates/serializavel.hpp"
#include "PaginaB.hpp"
#include "helpersArvore.hpp"

#include <iostream>
#include <algorithm>

using namespace std;

/**
 * @brief Classe com as características da página da árvore B+.
 * 
 * <p>Caso for usar o método mostrar() dá classe, é necessário que a chave e o dado
 * sejam tipos primitivos ou então o operador << deve ser sobrecarregado para que
 * seja possível inserir a chave e/ou o dado num ostream (output stream).</p>
 * 
 * @see [Sobrecarregando o operador <<](https://docs.microsoft.com/pt-br/cpp/standard-library/overloading-the-output-operator-for-your-own-classes?view=vs-2019)
 * 
 * @tparam TIPO_DAS_CHAVES Tipo da chave dos registros. <b>É necessário que a chave
 * seja um tipo primitivo ou então que a sua classe/struct herde de Serializavel e
 * tenha um construtor sem parâmetros.</b>
 * @tparam TIPO_DOS_DADOS Tipo do dado dos registros. <b>É necessário que o dado
 * seja um tipo primitivo ou então que a sua classe/struct herde de Serializavel e
 * tenha um construtor sem parâmetros.</b>
 */
template <typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
class PaginaBMais : public PaginaB<TIPO_DAS_CHAVES, TIPO_DOS_DADOS>
{
public:
    // ------------------------- Typedefs

    /**
     * @brief Padroniza o tipo da página da árvore. Typedefs dentro de classes ou
     * structs são considerados como boa prática em C++.
     */
    typedef PaginaBMais<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> Pagina;
    typedef PaginaB<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> PaginaHerdada;

    // ------------------------- Campos

    using PaginaHerdada::chaves;
    using PaginaHerdada::dados;
    using PaginaHerdada::ponteiros;

    file_ptr_type ptrProximaPagina = constantes::ptrNuloPagina;

    // ------------------------- Construtores

    // https://softwareengineering.stackexchange.com/questions/197893/why-are-constructors-not-inherited
    // Importa o construtor da classe ArvoreB
    using PaginaHerdada::PaginaB;

    // ------------------------- Métodos herdados de Serializavel

    int obterTamanhoMaximoEmBytes() override
    {
        // Chamar uma função da herdada da ArvoreB:
        // https://stackoverflow.com/questions/672373/can-i-call-a-base-classs-virtual-function-if-im-overriding-it
        // adiciona o tamanho do ponteiro para a próxima página
        return PaginaHerdada::obterTamanhoMaximoEmBytes() + sizeof(file_ptr_type);
    }

    DataOutputStream &gerarDataOutputStream(DataOutputStream &out) override
    {
        // Chamar uma função da herdada da ArvoreB:
        // https://stackoverflow.com/questions/672373/can-i-call-a-base-classs-virtual-function-if-im-overriding-it
        PaginaHerdada::gerarDataOutputStream(out);

        out << ptrProximaPagina;

        return out;
    }

    void lerBytes(DataInputStream &input) override
    {
        // Chamar uma função da herdada da ArvoreB:
        // https://stackoverflow.com/questions/672373/can-i-call-a-base-classs-virtual-function-if-im-overriding-it
        PaginaHerdada::lerBytes(input);

        input >> ptrProximaPagina;
    }

    // ------------------------- Métodos

    void limpar()
    {
        PaginaHerdada::limpar();
        ptrProximaPagina = constantes::ptrNuloPagina;
    }

    void transferirElementoPara(
        Pagina *paginaDestino, int indiceNoDestino, int indiceLocal,
        bool excluirPonteiroDaEsquerda = false, bool excluirPonteiroDaDireita = false,
        bool inserirPonteiroADireita = true)
    {
        paginaDestino->inserir(
            chaves[indiceLocal], dados[indiceLocal],
            indiceNoDestino, constantes::ptrNuloPagina,
            inserirPonteiroADireita
        );
    }

    void mostrar(ostream &ostream = cout,
               bool mostrarOsDados = false,
               bool mostrarOsPonteiros = true,
               bool mostrarEndereco = true,
               string delimitadorEntreOPonteiroEAChave = " (",
               string delimitadorEntreODadoEOPonteiro = ") ",
               string delimitadorEntreAChaveEODado = ", ")
    {
        PaginaHerdada::mostrar(
            ostream, mostrarOsDados, mostrarOsPonteiros, mostrarEndereco,
            delimitadorEntreOPonteiroEAChave,
            delimitadorEntreODadoEOPonteiro,
            delimitadorEntreAChaveEODado);

        ostream << " -> " << (long) ptrProximaPagina << endl;
    }
};
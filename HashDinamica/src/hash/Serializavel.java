package hash;/* See the project's root for license information. */

/**
 * Interface para padronização da forma de serializar objetos.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * 
 * @see <a href="https://pt.wikipedia.org/wiki/Serializa%C3%A7%C3%A3o">
 * Serialização – Wikipédia, a enciclopédia livre</a>
 */

public interface Serializavel
{
    /**
     * Calcula a quantidade máxima de bytes que o objeto pode gastar.
     * 
     * @return a quantidade máxima de bytes que o objeto pode gastar.
     */

    int obterTamanhoMaximoEmBytes();

    /**
     * Gera um vetor com os bytes do objeto.
     * 
     * @return um vetor com os bytes do objeto.
     */

    byte[] obterBytes();

    /**
     * Lê os bytes do vetor atribuindo os campos internos da entidade.
     * 
     * @param bytes Vetor com os bytes do objeto.
     */

    public void lerBytes(byte[] bytes);

    /**
     * Gera uma string com base nos campos internos da entidade.
     * 
     * @return uma string com base nos campos internos da entidade.
     */

    public String toString();
}

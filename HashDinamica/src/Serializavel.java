import java.io.RandomAccessFile;

public interface Serializavel
{
	/**
	 * Escreve os bytes do objeto na {@code correnteDeSaida}
	 * a partir de onde o cursor dela estiver.
	 * 
	 * @param correnteDeSaida Corrente de saída dos bytes.
	 */
	
	void escreverObjeto(RandomAccessFile correnteDeSaida);
	
	/**
	 * Lê os bytes do objeto da {@code correnteDeEntrada}
	 * a partir de onde o cursor dela estiver.
	 * 
	 * @param correnteDeEntrada Corrente de entrada dos bytes.
	 */
	
	void lerObjeto(RandomAccessFile correnteDeEntrada);
	
	/**
	 * Gera um vetor com os bytes do objeto.
	 * 
	 * @return um vetor com os bytes do objeto.
	 */
	
	byte[] obterBytes();
	
	/**
	 * Lê os bytes do objeto do vetor.
	 * 
	 * @param bytes Vetor com os bytes do objeto.
	 */
	
	void lerBytes(byte[] bytes);
}

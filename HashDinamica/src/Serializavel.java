import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface Serializavel
{
	/**
	 * Escreve os bytes do objeto na {@code correnteDeSaida}
	 * a partir de onde o cursor dela estiver.
	 * 
	 * @param correnteDeSaida Corrente de saída dos bytes.
	 */
	
	void escreverObjeto(ObjectOutputStream correnteDeSaida);
	
	/**
	 * Lê os bytes do objeto da {@code correnteDeEntrada}
	 * a partir de onde o cursor dela estiver.
	 * 
	 * @param correnteDeEntrada Corrente de entrada dos bytes.
	 */
	
	void lerObjeto(ObjectInputStream correnteDeEntrada);
	
	/**
	 * Gera um vetor com os bytes do objeto.
	 * 
	 * @return um vetor com os bytes do objeto.
	 */
	
	byte[] obterBytes();
	
	/**
	 * Lê os bytes do objeto do vetor {@code bytes}.
	 * 
	 * @param bytes Vetor com os bytes do objeto.
	 */
	
	void lerBytes(byte[] bytes);
}

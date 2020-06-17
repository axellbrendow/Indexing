package hash;/* See the project's root for license information. */

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializavelHelper
{
	/**
	 * Escreve os bytes do objeto na {@code correnteDeSaida}
	 * a partir de onde o cursor dela estiver.
	 * 
	 * @param serializavel Objeto serializável que será escrito no arquivo.
	 * @param correnteDeSaida Corrente de saída dos bytes.
	 */
	public static void escreverObjeto(Serializavel serializavel, RandomAccessFile correnteDeSaida)
	{
		try
		{
			correnteDeSaida.write(serializavel.obterBytes());
		}
		
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
	}
	
	/**
	 * Lê os bytes do objeto da {@code correnteDeEntrada}
	 * a partir de onde o cursor dela estiver.
	 * 
	 * @param serializavel Objeto serializável que ser lido do arquivo.
	 * @param correnteDeEntrada Corrente de entrada dos bytes.
	 */
	public static void lerObjeto(Serializavel serializavel, RandomAccessFile correnteDeEntrada)
	{
		try
		{
			byte[] registro = new byte[serializavel.obterTamanhoMaximoEmBytes()];
			
			correnteDeEntrada.read(registro);
			
			serializavel.lerBytes(registro);
		}
		
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
	}
	
	/**
	 * Escreve os bytes do objeto na {@code correnteDeSaida}
	 * a partir de um deslocamento.
	 * 
	 * @param serializavel Objeto serializável que irá gerar os bytes a serem escritos.
	 * @param correnteDeSaida Corrente de saída dos bytes.
	 * @param deslocamento Deslocamento em relação ao início.
	 */
	public static void escreverObjeto(Serializavel serializavel, byte[] correnteDeSaida, int deslocamento)
	{
		byte[] bytes = serializavel.obterBytes();
		
		System.arraycopy(bytes, 0, correnteDeSaida, deslocamento, bytes.length);
	}
	
	/**
	 * Lê os bytes do vetor, a partir de um deslocamento,
	 * atribuindo os campos internos da entidade.
	 * 
	 * @param serializavel Objeto serializável que irá ler os bytes.
	 * @param bytes Vetor com os bytes do objeto.
	 * @param deslocamento Deslocamento em relação ao início.
	 */
	public static void lerBytes(Serializavel serializavel, byte[] bytes, int deslocamento)
	{
		if (bytes != null)
		{
			int restante = bytes.length - deslocamento - 1;
			
			if (restante > 0 && restante <= bytes.length)
			{
				byte[] bytesAproveitaveis = new byte[restante];
				
				System.arraycopy(bytes, deslocamento, bytesAproveitaveis, 0, restante);
				
				serializavel.lerBytes(bytesAproveitaveis);
			}
		}
	}
}

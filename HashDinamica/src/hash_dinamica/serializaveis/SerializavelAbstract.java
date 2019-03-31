package hash_dinamica.serializaveis;

import java.io.IOException;
import java.io.RandomAccessFile;

import hash_dinamica.Serializavel;

public abstract class SerializavelAbstract implements Serializavel
{
	/**
	 * Escreve os bytes do objeto na {@code correnteDeSaida}
	 * a partir de onde o cursor dela estiver.
	 * 
	 * @param correnteDeSaida Corrente de saída dos bytes.
	 */
	
	void escreverObjeto(RandomAccessFile correnteDeSaida)
	{
		try
		{
			correnteDeSaida.write(obterBytes());
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
	 * @param correnteDeEntrada Corrente de entrada dos bytes.
	 */
	
	public void lerObjeto(RandomAccessFile correnteDeEntrada)
	{
		try
		{
			byte[] registro = new byte[obterTamanhoMaximoEmBytes()];
			
			correnteDeEntrada.read(registro);
			
			lerBytes(registro);
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
	 * @param correnteDeSaida Corrente de saída dos bytes.
	 */
	
	public void escreverObjeto(byte[] correnteDeSaida, int deslocamento)
	{
		byte[] bytes = obterBytes();
		
		System.arraycopy(bytes, 0, correnteDeSaida, deslocamento, bytes.length);
	}
	
	/**
	 * Lê os bytes do vetor, a partir de um deslocamento,
	 * atribuindo os campos internos da entidade.
	 * 
	 * @param bytes Vetor com os bytes do objeto.
	 * @param deslocamento Deslocamento em relação ao início.
	 */
	
	void lerBytes(byte[] bytes, int deslocamento)
	{
		if (bytes != null)
		{
			int restante = bytes.length - deslocamento;
			
			if (restante > 0 && restante <= bytes.length)
			{
				byte[] bytesAproveitaveis = new byte[restante];
				
				System.arraycopy(bytes, 0, bytesAproveitaveis, 0, restante);
				
				lerBytes(bytesAproveitaveis);
			}
		}
	}
}

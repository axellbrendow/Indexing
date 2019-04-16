/*
MIT License

Copyright (c) 2019 Axell Brendow Batista Moreira

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package serializaveis;

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
	
	public void escreverObjeto(RandomAccessFile correnteDeSaida)
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
	 * @param deslocamento Deslocamento em relação ao início.
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
	
	public void lerBytes(byte[] bytes, int deslocamento)
	{
		if (bytes != null)
		{
			int restante = bytes.length - deslocamento;
			
			if (restante > 0 && restante <= bytes.length)
			{
				byte[] bytesAproveitaveis = new byte[restante];
				
				System.arraycopy(bytes, deslocamento, bytesAproveitaveis, 0, restante);
				
				lerBytes(bytesAproveitaveis);
			}
		}
	}
}

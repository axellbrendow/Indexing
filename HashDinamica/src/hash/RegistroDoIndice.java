/* See the project's root for license information. */

package hash;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import hash.serializaveis.SerializavelAbstract;

/**
 * Classe utilitária para manusear os registros de indices em um bucket.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 *
 * @param <TIPO_DAS_CHAVES> Classe da chave.
 * @param <TIPO_DOS_DADOS> Classe do dado.
 */

public class RegistroDoIndice<TIPO_DAS_CHAVES extends SerializavelAbstract, TIPO_DOS_DADOS extends SerializavelAbstract> extends SerializavelAbstract
{
	public static final char REGISTRO_ATIVADO = ' ';
	public static final char REGISTRO_DESATIVADO = '*';
	
	protected char lapide;
	protected TIPO_DAS_CHAVES chave;
	protected TIPO_DOS_DADOS dado;
	protected short quantidadeMaximaDeBytesParaAChave;
	protected short quantidadeMaximaDeBytesParaODado;
	protected Constructor<TIPO_DAS_CHAVES> construtorDaChave;
	protected Constructor<TIPO_DOS_DADOS> construtorDoDado;
	
	/**
	 * Cria um objeto que gerencia um registro de indice no bucket da hash dinâmica.
	 * 
	 * @param lapide Lapide do registro.
	 * @param chave Chave do registro.
	 * @param dado Dado que corresponde à chave.
	 * @param quantidadeMaximaDeBytesParaAChave Tamanho máximo que a chave pode gastar.
	 * @param quantidadeMaximaDeBytesParaODado Tamanho máximo que o dado pode gastar.
	 * @param construtorDaChave Construtor da chave. É necessário que a chave tenha um
	 * construtor sem parâmetros.
	 * @param construtorDoDado Construtor do dado. É necessário que o dado tenha um
	 * construtor sem parâmetros.
	 */
	
	public RegistroDoIndice(
		char lapide,
		TIPO_DAS_CHAVES chave,
		TIPO_DOS_DADOS dado,
		short quantidadeMaximaDeBytesParaAChave,
		short quantidadeMaximaDeBytesParaODado,
		Constructor<TIPO_DAS_CHAVES> construtorDaChave,
		Constructor<TIPO_DOS_DADOS> construtorDoDado)
	{
		this.lapide = lapide;
		this.chave = chave;
		this.dado = dado;
		this.quantidadeMaximaDeBytesParaAChave = quantidadeMaximaDeBytesParaAChave;
		this.quantidadeMaximaDeBytesParaODado = quantidadeMaximaDeBytesParaODado;
		this.construtorDaChave = construtorDaChave;
		this.construtorDoDado = construtorDoDado;
	}
	
	/**
	 * Calcula o tamanho que cada registro de indice gasta no bucket.
	 * 
	 * @return o tamanho que cada registro de indice gasta no bucket.
	 */
	
	@Override
	public int obterTamanhoMaximoEmBytes()
	{
		int tamanho = 0;
		
		if (quantidadeMaximaDeBytesParaAChave > 0 &&
			quantidadeMaximaDeBytesParaODado > 0)
		{
			tamanho = Byte.BYTES + // tamanho da lapide
				quantidadeMaximaDeBytesParaAChave + // tamanho da chave
				quantidadeMaximaDeBytesParaODado; // tamanho do dado
		}
		
		return tamanho;
	}
	
	/**
	 * Preenche com zeros a saída de dados até que o espaço
	 * usado seja igual ao tamanho máximo.
	 * 
	 * @param dataOutputStream Corrente de saída de dados.
	 * @param tamanhoUsado Quantidade de bytes já escritos.
	 * @param tamanhoMaximo Tamanho máximo desejado.
	 */
	
	private void completarEspacoNaoUsado(
		DataOutputStream dataOutputStream,
		int tamanhoUsado,
		int tamanhoMaximo)
	{
		int restante = tamanhoMaximo - tamanhoUsado;
		
		if (restante > 0)
		{
			byte[] lixo = new byte[restante];
			
			Arrays.fill(lixo, (byte) 0);
			
			try
			{
				dataOutputStream.write(lixo);
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public byte[] obterBytes()
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		
		try
		{
			dataOutputStream.writeByte(lapide);
			
			byte[] byteArrayChave = chave.obterBytes();
			
			dataOutputStream.write(byteArrayChave);
			
			completarEspacoNaoUsado(
				dataOutputStream,
				byteArrayChave.length,
				quantidadeMaximaDeBytesParaAChave
			);
			
			byte[] byteArrayDado = dado.obterBytes();
			
			dataOutputStream.write(byteArrayDado);
			
			completarEspacoNaoUsado(
				dataOutputStream,
				byteArrayDado.length,
				quantidadeMaximaDeBytesParaODado
			);
			
			dataOutputStream.close();
		}
		
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
		
		return byteArrayOutputStream.toByteArray();
	}

	@Override
	public void lerBytes(byte[] bytes)
	{
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
		
		try
		{
			lapide = (char) dataInputStream.readByte();
			
			byte[] byteArrayChave = new byte[quantidadeMaximaDeBytesParaAChave];
			byte[] byteArrayDado = new byte[quantidadeMaximaDeBytesParaODado];
			
			dataInputStream.read(byteArrayChave);
			dataInputStream.read(byteArrayDado);
			
			chave = construtorDaChave.newInstance();
			dado = construtorDoDado.newInstance();
			
			chave.lerBytes(byteArrayChave);
			dado.lerBytes(byteArrayDado);
			
			byteArrayInputStream.close();
			dataInputStream.close();
		}
		
		catch (InstantiationException |
				IllegalAccessException |
				IllegalArgumentException |
				InvocationTargetException |
				IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public String toString(String delimitadorEntreOsCamposDoRegistro, boolean mostrarApenasAChave)
	{
		return mostrarApenasAChave ?
			(
    			chave.toString()
			) :
			(
    			"'" + lapide + "'" + delimitadorEntreOsCamposDoRegistro +
    			chave + delimitadorEntreOsCamposDoRegistro +
    			dado
			);
	}
	
	@Override
	public String toString()
	{
		return toString(", ", false);
	}
}

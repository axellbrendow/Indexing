import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Classe que gerencia um bucket específico.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class Bucket implements Serializavel
{
	private byte profundidadeLocal;
	private int numeroDeRegistrosPorBucket;
	private short quantidadeMaximaDeBytesParaAChave;
	private short quantidadeMaximaDeBytesParaODado;
	//RegistroDeIndice<TIPO_DAS_CHAVES, TIPO_DOS_DADOS>[] registrosDosIndices;
	byte[] bucket;
	
	public Bucket(
			byte profundidadeLocal,
			int numeroDeRegistrosPorBucket,
			short quantidadeMaximaDeBytesParaAChave,
			short quantidadeMaximaDeBytesParaODado)
			//RegistroDeIndice<TIPO_DAS_CHAVES, TIPO_DOS_DADOS>[] registrosDosIndices)
	{
		this.profundidadeLocal = profundidadeLocal;
		this.numeroDeRegistrosPorBucket = numeroDeRegistrosPorBucket;
		this.quantidadeMaximaDeBytesParaAChave = quantidadeMaximaDeBytesParaAChave;
		this.quantidadeMaximaDeBytesParaODado = quantidadeMaximaDeBytesParaODado;
		//this.registrosDosIndices = registrosDosIndices;
	}
	
	/**
	 * Calcula o tamanho que cada registro de indice gasta no bucket.
	 * 
	 * @return o tamanho que cada registro de indice gasta no bucket.
	 */
	
	private int obterTamanhoDoRegistroDeUmIndice()
	{
		int tamanho = 0;
		
		if (numeroDeRegistrosPorBucket > 0 &&
			quantidadeMaximaDeBytesParaAChave > 0 &&
			quantidadeMaximaDeBytesParaODado > 0)
		{
			tamanho = Byte.BYTES + // tamanho da lapide
					quantidadeMaximaDeBytesParaAChave + // tamanho da chave
					quantidadeMaximaDeBytesParaODado; // tamanho do dado
		}
		
		return tamanho;
	}
	
	private int obterTamanhoDeUmBucket()
	{
		return Byte.BYTES + // tamanho da profundidade local
				numeroDeRegistrosPorBucket * obterTamanhoDoRegistroDeUmIndice();
	}
	
	@Override
	public void escreverObjeto(RandomAccessFile correnteDeSaida)
	{
		if (correnteDeSaida != null)
		{
			byte[] bytes = obterBytes();
			
			try
			{
				correnteDeSaida.write(bytes);
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
	}
	
	@Override
	public void lerObjeto(RandomAccessFile correnteDeEntrada)
	{
		if (correnteDeEntrada != null)
		{
			try
			{
				profundidadeLocal = correnteDeEntrada.readByte();
				
				bucket = new byte[obterTamanhoDeUmBucket()];
				
				correnteDeEntrada.readFully(bucket);
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
	}
	
	@Override
	public byte[] obterBytes()
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size)
	}
	
	@Override
	public void lerBytes(byte[] bytes)
	{
		// TODO Auto-generated method stub
		
	}
}

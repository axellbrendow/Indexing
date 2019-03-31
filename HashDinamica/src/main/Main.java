package main;
import java.io.File;
import entities.entity;
import hash_dinamica.HashDinamica;
import util.IO;

public class Main
{
	public static final String NOME_DO_ARQUIVO_DO_DIRETORIO = "diretorio.dir";
	public static final String NOME_DO_ARQUIVO_DOS_BUCKETS = "buckets.db";
	public static final File ARQUIVO_DO_DIRETORIO = new File(NOME_DO_ARQUIVO_DO_DIRETORIO);
	public static final File ARQUIVO_DOS_BUCKETS = new File(NOME_DO_ARQUIVO_DOS_BUCKETS);
	
	public static void main(String[] args)
	{
		ARQUIVO_DO_DIRETORIO.delete();
		ARQUIVO_DOS_BUCKETS.delete();
		
		entity ent1 = new entity(1, "entity 1");
		entity ent2 = new entity(2, "entity 2");
		entity ent3 = new entity(3, "entity 3");
		entity ent4 = new entity(4, "entity 4");
		entity ent5 = new entity(5, "entity 5");
		entity ent6 = new entity(6, "entity 6");
		
		try
		{
			// olhar problema de chaves com hashs iguais num mesmo bucket
			HashDinamica<entity, entity> hash = new HashDinamica<entity, entity>(
				NOME_DO_ARQUIVO_DO_DIRETORIO,
				NOME_DO_ARQUIVO_DOS_BUCKETS,
				3,
				(short) (Integer.BYTES + 300),
				(short) (Integer.BYTES + 300),
				entity.class.getConstructor(),
				entity.class.getConstructor(),
				(ent) -> { return ent.id; }
			);
			
			hash.inserir(ent1, ent2);
			hash.inserir(ent1, ent3);
			hash.inserir(ent1, ent4);
			hash.excluir(ent1, ent2);
			hash.inserir(ent5, ent6);
			hash.inserir(ent4, ent2);
			hash.inserir(ent3, ent3);
			hash.inserir(ent2, ent4);
			
			hash.listarDadosComAChave(ent1).forEach( (it) -> { IO.println(it); } );
		}
		
		catch (NoSuchMethodException | SecurityException e)
		{
			e.printStackTrace();
		}
	}

}

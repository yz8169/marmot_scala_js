package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import com.github.tototoshi.slick.MySQLJodaSupport._
  import org.joda.time.DateTime
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(Allinfo.schema, Basic.schema, Cds.schema, Fpkm.schema, Fpkmmessage.schema, GeneBlock.schema, GeneGo.schema, GeneKegg.schema, Go.schema, HiberList.schema, HibernationDiffGene.schema, Kegg.schema, Mode.schema, Orthologs.schema, OtherOrthologs.schema, Pep.schema, PlagueList.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Allinfo
   *  @param geneid Database column geneId SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param genename Database column geneName SqlType(VARCHAR), Length(255,true)
   *  @param length Database column length SqlType(INT)
   *  @param chr Database column chr SqlType(VARCHAR), Length(255,true)
   *  @param start Database column start SqlType(INT)
   *  @param end Database column end SqlType(INT)
   *  @param strand Database column strand SqlType(VARCHAR), Length(255,true)
   *  @param cogclass Database column cogClass SqlType(VARCHAR), Length(255,true)
   *  @param cogclassanno Database column cogClassAnno SqlType(VARCHAR), Length(255,true)
   *  @param goterm Database column goTerm SqlType(TEXT)
   *  @param goanno Database column goAnno SqlType(TEXT)
   *  @param keggterm Database column keggTerm SqlType(TEXT)
   *  @param kegganno Database column keggAnno SqlType(TEXT)
   *  @param kogclass Database column kogClass SqlType(VARCHAR), Length(255,true)
   *  @param kogclassanno Database column kogClassAnno SqlType(VARCHAR), Length(255,true)
   *  @param pfamanno Database column pfamAnno SqlType(TEXT)
   *  @param swissprotanno Database column swissprotAnno SqlType(VARCHAR), Length(255,true)
   *  @param trenbkanno Database column trenbkAnno SqlType(VARCHAR), Length(255,true)
   *  @param eggNogClass Database column egg_nog_class SqlType(TEXT)
   *  @param eggNogClassAnno Database column egg_nog_class_anno SqlType(TEXT)
   *  @param nranno Database column nrAnno SqlType(TEXT)
   *  @param ntanno Database column ntAnno SqlType(TEXT) */
  case class AllinfoRow(geneid: String, genename: String, length: Int, chr: String, start: Int, end: Int, strand: String, cogclass: String, cogclassanno: String, goterm: String, goanno: String, keggterm: String, kegganno: String, kogclass: String, kogclassanno: String, pfamanno: String, swissprotanno: String, trenbkanno: String, eggNogClass: String, eggNogClassAnno: String, nranno: String, ntanno: String)
  /** GetResult implicit for fetching AllinfoRow objects using plain SQL queries */
  implicit def GetResultAllinfoRow(implicit e0: GR[String], e1: GR[Int]): GR[AllinfoRow] = GR{
    prs => import prs._
    AllinfoRow.tupled((<<[String], <<[String], <<[Int], <<[String], <<[Int], <<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table allinfo. Objects of this class serve as prototypes for rows in queries. */
  class Allinfo(_tableTag: Tag) extends profile.api.Table[AllinfoRow](_tableTag, Some("mardb"), "allinfo") {
    def * = (geneid, genename, length, chr, start, end, strand, cogclass, cogclassanno, goterm, goanno, keggterm, kegganno, kogclass, kogclassanno, pfamanno, swissprotanno, trenbkanno, eggNogClass, eggNogClassAnno, nranno, ntanno) <> (AllinfoRow.tupled, AllinfoRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(geneid), Rep.Some(genename), Rep.Some(length), Rep.Some(chr), Rep.Some(start), Rep.Some(end), Rep.Some(strand), Rep.Some(cogclass), Rep.Some(cogclassanno), Rep.Some(goterm), Rep.Some(goanno), Rep.Some(keggterm), Rep.Some(kegganno), Rep.Some(kogclass), Rep.Some(kogclassanno), Rep.Some(pfamanno), Rep.Some(swissprotanno), Rep.Some(trenbkanno), Rep.Some(eggNogClass), Rep.Some(eggNogClassAnno), Rep.Some(nranno), Rep.Some(ntanno)).shaped.<>({r=>import r._; _1.map(_=> AllinfoRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get, _17.get, _18.get, _19.get, _20.get, _21.get, _22.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column geneId SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val geneid: Rep[String] = column[String]("geneId", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column geneName SqlType(VARCHAR), Length(255,true) */
    val genename: Rep[String] = column[String]("geneName", O.Length(255,varying=true))
    /** Database column length SqlType(INT) */
    val length: Rep[Int] = column[Int]("length")
    /** Database column chr SqlType(VARCHAR), Length(255,true) */
    val chr: Rep[String] = column[String]("chr", O.Length(255,varying=true))
    /** Database column start SqlType(INT) */
    val start: Rep[Int] = column[Int]("start")
    /** Database column end SqlType(INT) */
    val end: Rep[Int] = column[Int]("end")
    /** Database column strand SqlType(VARCHAR), Length(255,true) */
    val strand: Rep[String] = column[String]("strand", O.Length(255,varying=true))
    /** Database column cogClass SqlType(VARCHAR), Length(255,true) */
    val cogclass: Rep[String] = column[String]("cogClass", O.Length(255,varying=true))
    /** Database column cogClassAnno SqlType(VARCHAR), Length(255,true) */
    val cogclassanno: Rep[String] = column[String]("cogClassAnno", O.Length(255,varying=true))
    /** Database column goTerm SqlType(TEXT) */
    val goterm: Rep[String] = column[String]("goTerm")
    /** Database column goAnno SqlType(TEXT) */
    val goanno: Rep[String] = column[String]("goAnno")
    /** Database column keggTerm SqlType(TEXT) */
    val keggterm: Rep[String] = column[String]("keggTerm")
    /** Database column keggAnno SqlType(TEXT) */
    val kegganno: Rep[String] = column[String]("keggAnno")
    /** Database column kogClass SqlType(VARCHAR), Length(255,true) */
    val kogclass: Rep[String] = column[String]("kogClass", O.Length(255,varying=true))
    /** Database column kogClassAnno SqlType(VARCHAR), Length(255,true) */
    val kogclassanno: Rep[String] = column[String]("kogClassAnno", O.Length(255,varying=true))
    /** Database column pfamAnno SqlType(TEXT) */
    val pfamanno: Rep[String] = column[String]("pfamAnno")
    /** Database column swissprotAnno SqlType(VARCHAR), Length(255,true) */
    val swissprotanno: Rep[String] = column[String]("swissprotAnno", O.Length(255,varying=true))
    /** Database column trenbkAnno SqlType(VARCHAR), Length(255,true) */
    val trenbkanno: Rep[String] = column[String]("trenbkAnno", O.Length(255,varying=true))
    /** Database column egg_nog_class SqlType(TEXT) */
    val eggNogClass: Rep[String] = column[String]("egg_nog_class")
    /** Database column egg_nog_class_anno SqlType(TEXT) */
    val eggNogClassAnno: Rep[String] = column[String]("egg_nog_class_anno")
    /** Database column nrAnno SqlType(TEXT) */
    val nranno: Rep[String] = column[String]("nrAnno")
    /** Database column ntAnno SqlType(TEXT) */
    val ntanno: Rep[String] = column[String]("ntAnno")

    /** Index over (chr) (database name chr) */
    val index1 = index("chr", chr)
    /** Index over (genename) (database name geneName) */
    val index2 = index("geneName", genename)
  }
  /** Collection-like TableQuery object for table Allinfo */
  lazy val Allinfo = new TableQuery(tag => new Allinfo(tag))

  /** Entity class storing rows of table Basic
   *  @param id Database column id SqlType(VARCHAR), Length(255,true)
   *  @param species Database column species SqlType(VARCHAR), Length(255,true)
   *  @param symbol Database column symbol SqlType(VARCHAR), Length(255,true)
   *  @param description Database column description SqlType(TEXT)
   *  @param chr Database column chr SqlType(VARCHAR), Length(255,true)
   *  @param start Database column start SqlType(INT)
   *  @param end Database column end SqlType(INT)
   *  @param strand Database column strand SqlType(VARCHAR), Length(255,true) */
  case class BasicRow(id: String, species: String, symbol: String, description: String, chr: String, start: Int, end: Int, strand: String)
  /** GetResult implicit for fetching BasicRow objects using plain SQL queries */
  implicit def GetResultBasicRow(implicit e0: GR[String], e1: GR[Int]): GR[BasicRow] = GR{
    prs => import prs._
    BasicRow.tupled((<<[String], <<[String], <<[String], <<[String], <<[String], <<[Int], <<[Int], <<[String]))
  }
  /** Table description of table basic. Objects of this class serve as prototypes for rows in queries. */
  class Basic(_tableTag: Tag) extends profile.api.Table[BasicRow](_tableTag, Some("mardb"), "basic") {
    def * = (id, species, symbol, description, chr, start, end, strand) <> (BasicRow.tupled, BasicRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(species), Rep.Some(symbol), Rep.Some(description), Rep.Some(chr), Rep.Some(start), Rep.Some(end), Rep.Some(strand)).shaped.<>({r=>import r._; _1.map(_=> BasicRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(VARCHAR), Length(255,true) */
    val id: Rep[String] = column[String]("id", O.Length(255,varying=true))
    /** Database column species SqlType(VARCHAR), Length(255,true) */
    val species: Rep[String] = column[String]("species", O.Length(255,varying=true))
    /** Database column symbol SqlType(VARCHAR), Length(255,true) */
    val symbol: Rep[String] = column[String]("symbol", O.Length(255,varying=true))
    /** Database column description SqlType(TEXT) */
    val description: Rep[String] = column[String]("description")
    /** Database column chr SqlType(VARCHAR), Length(255,true) */
    val chr: Rep[String] = column[String]("chr", O.Length(255,varying=true))
    /** Database column start SqlType(INT) */
    val start: Rep[Int] = column[Int]("start")
    /** Database column end SqlType(INT) */
    val end: Rep[Int] = column[Int]("end")
    /** Database column strand SqlType(VARCHAR), Length(255,true) */
    val strand: Rep[String] = column[String]("strand", O.Length(255,varying=true))

    /** Primary key of Basic (database name basic_PK) */
    val pk = primaryKey("basic_PK", (id, species))
  }
  /** Collection-like TableQuery object for table Basic */
  lazy val Basic = new TableQuery(tag => new Basic(tag))

  /** Entity class storing rows of table Cds
   *  @param geneid Database column geneId SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param cds Database column cds SqlType(LONGTEXT), Length(2147483647,true) */
  case class CdsRow(geneid: String, cds: String)
  /** GetResult implicit for fetching CdsRow objects using plain SQL queries */
  implicit def GetResultCdsRow(implicit e0: GR[String]): GR[CdsRow] = GR{
    prs => import prs._
    CdsRow.tupled((<<[String], <<[String]))
  }
  /** Table description of table cds. Objects of this class serve as prototypes for rows in queries. */
  class Cds(_tableTag: Tag) extends profile.api.Table[CdsRow](_tableTag, Some("mardb"), "cds") {
    def * = (geneid, cds) <> (CdsRow.tupled, CdsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(geneid), Rep.Some(cds)).shaped.<>({r=>import r._; _1.map(_=> CdsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column geneId SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val geneid: Rep[String] = column[String]("geneId", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column cds SqlType(LONGTEXT), Length(2147483647,true) */
    val cds: Rep[String] = column[String]("cds", O.Length(2147483647,varying=true))
  }
  /** Collection-like TableQuery object for table Cds */
  lazy val Cds = new TableQuery(tag => new Cds(tag))

  /** Entity class storing rows of table Fpkm
   *  @param geneid Database column geneId SqlType(VARCHAR), Length(255,true)
   *  @param samplename Database column sampleName SqlType(VARCHAR), Length(255,true)
   *  @param value Database column value SqlType(DOUBLE)
   *  @param group Database column group SqlType(VARCHAR), Length(255,true) */
  case class FpkmRow(geneid: String, samplename: String, value: Double, group: String)
  /** GetResult implicit for fetching FpkmRow objects using plain SQL queries */
  implicit def GetResultFpkmRow(implicit e0: GR[String], e1: GR[Double]): GR[FpkmRow] = GR{
    prs => import prs._
    FpkmRow.tupled((<<[String], <<[String], <<[Double], <<[String]))
  }
  /** Table description of table fpkm. Objects of this class serve as prototypes for rows in queries. */
  class Fpkm(_tableTag: Tag) extends profile.api.Table[FpkmRow](_tableTag, Some("mardb"), "fpkm") {
    def * = (geneid, samplename, value, group) <> (FpkmRow.tupled, FpkmRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(geneid), Rep.Some(samplename), Rep.Some(value), Rep.Some(group)).shaped.<>({r=>import r._; _1.map(_=> FpkmRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column geneId SqlType(VARCHAR), Length(255,true) */
    val geneid: Rep[String] = column[String]("geneId", O.Length(255,varying=true))
    /** Database column sampleName SqlType(VARCHAR), Length(255,true) */
    val samplename: Rep[String] = column[String]("sampleName", O.Length(255,varying=true))
    /** Database column value SqlType(DOUBLE) */
    val value: Rep[Double] = column[Double]("value")
    /** Database column group SqlType(VARCHAR), Length(255,true) */
    val group: Rep[String] = column[String]("group", O.Length(255,varying=true))

    /** Primary key of Fpkm (database name fpkm_PK) */
    val pk = primaryKey("fpkm_PK", (geneid, samplename, group))

    /** Index over (samplename) (database name sampleName) */
    val index1 = index("sampleName", samplename)
  }
  /** Collection-like TableQuery object for table Fpkm */
  lazy val Fpkm = new TableQuery(tag => new Fpkm(tag))

  /** Entity class storing rows of table Fpkmmessage
   *  @param geneid Database column geneId SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param length Database column length SqlType(INT)
   *  @param chr Database column chr SqlType(VARCHAR), Length(255,true)
   *  @param start Database column start SqlType(INT)
   *  @param end Database column end SqlType(INT)
   *  @param strand Database column strand SqlType(VARCHAR), Length(255,true) */
  case class FpkmmessageRow(geneid: String, length: Int, chr: String, start: Int, end: Int, strand: String)
  /** GetResult implicit for fetching FpkmmessageRow objects using plain SQL queries */
  implicit def GetResultFpkmmessageRow(implicit e0: GR[String], e1: GR[Int]): GR[FpkmmessageRow] = GR{
    prs => import prs._
    FpkmmessageRow.tupled((<<[String], <<[Int], <<[String], <<[Int], <<[Int], <<[String]))
  }
  /** Table description of table fpkmmessage. Objects of this class serve as prototypes for rows in queries. */
  class Fpkmmessage(_tableTag: Tag) extends profile.api.Table[FpkmmessageRow](_tableTag, Some("mardb"), "fpkmmessage") {
    def * = (geneid, length, chr, start, end, strand) <> (FpkmmessageRow.tupled, FpkmmessageRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(geneid), Rep.Some(length), Rep.Some(chr), Rep.Some(start), Rep.Some(end), Rep.Some(strand)).shaped.<>({r=>import r._; _1.map(_=> FpkmmessageRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column geneId SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val geneid: Rep[String] = column[String]("geneId", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column length SqlType(INT) */
    val length: Rep[Int] = column[Int]("length")
    /** Database column chr SqlType(VARCHAR), Length(255,true) */
    val chr: Rep[String] = column[String]("chr", O.Length(255,varying=true))
    /** Database column start SqlType(INT) */
    val start: Rep[Int] = column[Int]("start")
    /** Database column end SqlType(INT) */
    val end: Rep[Int] = column[Int]("end")
    /** Database column strand SqlType(VARCHAR), Length(255,true) */
    val strand: Rep[String] = column[String]("strand", O.Length(255,varying=true))

    /** Index over (chr) (database name chr) */
    val index1 = index("chr", chr)
  }
  /** Collection-like TableQuery object for table Fpkmmessage */
  lazy val Fpkmmessage = new TableQuery(tag => new Fpkmmessage(tag))

  /** Entity class storing rows of table GeneBlock
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param aGeneId Database column a_gene_id SqlType(VARCHAR), Length(255,true)
   *  @param kind Database column kind SqlType(VARCHAR), Length(255,true)
   *  @param aChr Database column a_chr SqlType(VARCHAR), Length(255,true)
   *  @param aStart Database column a_start SqlType(INT)
   *  @param aEnd Database column a_end SqlType(INT)
   *  @param aStrand Database column a_strand SqlType(VARCHAR), Length(255,true)
   *  @param bGeneId Database column b_gene_id SqlType(VARCHAR), Length(255,true)
   *  @param bGeneName Database column b_gene_name SqlType(VARCHAR), Length(255,true)
   *  @param bChr Database column b_chr SqlType(VARCHAR), Length(255,true)
   *  @param bStart Database column b_start SqlType(INT)
   *  @param bEnd Database column b_end SqlType(INT)
   *  @param bStrand Database column b_strand SqlType(VARCHAR), Length(255,true)
   *  @param proteinId Database column protein_id SqlType(VARCHAR), Length(255,true)
   *  @param mRnaId Database column m_rna_id SqlType(VARCHAR), Length(255,true)
   *  @param blockName Database column block_name SqlType(VARCHAR), Length(255,true) */
  case class GeneBlockRow(id: Int, aGeneId: String, kind: String, aChr: String, aStart: Int, aEnd: Int, aStrand: String, bGeneId: String, bGeneName: String, bChr: String, bStart: Int, bEnd: Int, bStrand: String, proteinId: String, mRnaId: String, blockName: String)
  /** GetResult implicit for fetching GeneBlockRow objects using plain SQL queries */
  implicit def GetResultGeneBlockRow(implicit e0: GR[Int], e1: GR[String]): GR[GeneBlockRow] = GR{
    prs => import prs._
    GeneBlockRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[Int], <<[Int], <<[String], <<[String], <<[String], <<[String], <<[Int], <<[Int], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table gene_block. Objects of this class serve as prototypes for rows in queries. */
  class GeneBlock(_tableTag: Tag) extends profile.api.Table[GeneBlockRow](_tableTag, Some("mardb"), "gene_block") {
    def * = (id, aGeneId, kind, aChr, aStart, aEnd, aStrand, bGeneId, bGeneName, bChr, bStart, bEnd, bStrand, proteinId, mRnaId, blockName) <> (GeneBlockRow.tupled, GeneBlockRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(aGeneId), Rep.Some(kind), Rep.Some(aChr), Rep.Some(aStart), Rep.Some(aEnd), Rep.Some(aStrand), Rep.Some(bGeneId), Rep.Some(bGeneName), Rep.Some(bChr), Rep.Some(bStart), Rep.Some(bEnd), Rep.Some(bStrand), Rep.Some(proteinId), Rep.Some(mRnaId), Rep.Some(blockName)).shaped.<>({r=>import r._; _1.map(_=> GeneBlockRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column a_gene_id SqlType(VARCHAR), Length(255,true) */
    val aGeneId: Rep[String] = column[String]("a_gene_id", O.Length(255,varying=true))
    /** Database column kind SqlType(VARCHAR), Length(255,true) */
    val kind: Rep[String] = column[String]("kind", O.Length(255,varying=true))
    /** Database column a_chr SqlType(VARCHAR), Length(255,true) */
    val aChr: Rep[String] = column[String]("a_chr", O.Length(255,varying=true))
    /** Database column a_start SqlType(INT) */
    val aStart: Rep[Int] = column[Int]("a_start")
    /** Database column a_end SqlType(INT) */
    val aEnd: Rep[Int] = column[Int]("a_end")
    /** Database column a_strand SqlType(VARCHAR), Length(255,true) */
    val aStrand: Rep[String] = column[String]("a_strand", O.Length(255,varying=true))
    /** Database column b_gene_id SqlType(VARCHAR), Length(255,true) */
    val bGeneId: Rep[String] = column[String]("b_gene_id", O.Length(255,varying=true))
    /** Database column b_gene_name SqlType(VARCHAR), Length(255,true) */
    val bGeneName: Rep[String] = column[String]("b_gene_name", O.Length(255,varying=true))
    /** Database column b_chr SqlType(VARCHAR), Length(255,true) */
    val bChr: Rep[String] = column[String]("b_chr", O.Length(255,varying=true))
    /** Database column b_start SqlType(INT) */
    val bStart: Rep[Int] = column[Int]("b_start")
    /** Database column b_end SqlType(INT) */
    val bEnd: Rep[Int] = column[Int]("b_end")
    /** Database column b_strand SqlType(VARCHAR), Length(255,true) */
    val bStrand: Rep[String] = column[String]("b_strand", O.Length(255,varying=true))
    /** Database column protein_id SqlType(VARCHAR), Length(255,true) */
    val proteinId: Rep[String] = column[String]("protein_id", O.Length(255,varying=true))
    /** Database column m_rna_id SqlType(VARCHAR), Length(255,true) */
    val mRnaId: Rep[String] = column[String]("m_rna_id", O.Length(255,varying=true))
    /** Database column block_name SqlType(VARCHAR), Length(255,true) */
    val blockName: Rep[String] = column[String]("block_name", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table GeneBlock */
  lazy val GeneBlock = new TableQuery(tag => new GeneBlock(tag))

  /** Entity class storing rows of table GeneGo
   *  @param geneId Database column gene_id SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param content Database column content SqlType(LONGTEXT), Length(2147483647,true) */
  case class GeneGoRow(geneId: String, content: String)
  /** GetResult implicit for fetching GeneGoRow objects using plain SQL queries */
  implicit def GetResultGeneGoRow(implicit e0: GR[String]): GR[GeneGoRow] = GR{
    prs => import prs._
    GeneGoRow.tupled((<<[String], <<[String]))
  }
  /** Table description of table gene_go. Objects of this class serve as prototypes for rows in queries. */
  class GeneGo(_tableTag: Tag) extends profile.api.Table[GeneGoRow](_tableTag, Some("mardb"), "gene_go") {
    def * = (geneId, content) <> (GeneGoRow.tupled, GeneGoRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(geneId), Rep.Some(content)).shaped.<>({r=>import r._; _1.map(_=> GeneGoRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column gene_id SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val geneId: Rep[String] = column[String]("gene_id", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column content SqlType(LONGTEXT), Length(2147483647,true) */
    val content: Rep[String] = column[String]("content", O.Length(2147483647,varying=true))

    /** Index over (geneId,content) (database name full_text) */
    val index1 = index("full_text", (geneId, content))
  }
  /** Collection-like TableQuery object for table GeneGo */
  lazy val GeneGo = new TableQuery(tag => new GeneGo(tag))

  /** Entity class storing rows of table GeneKegg
   *  @param geneId Database column gene_id SqlType(VARCHAR), Length(255,true)
   *  @param ko Database column ko SqlType(VARCHAR), Length(255,true)
   *  @param description Database column description SqlType(TEXT) */
  case class GeneKeggRow(geneId: String, ko: String, description: String)
  /** GetResult implicit for fetching GeneKeggRow objects using plain SQL queries */
  implicit def GetResultGeneKeggRow(implicit e0: GR[String]): GR[GeneKeggRow] = GR{
    prs => import prs._
    GeneKeggRow.tupled((<<[String], <<[String], <<[String]))
  }
  /** Table description of table gene_kegg. Objects of this class serve as prototypes for rows in queries. */
  class GeneKegg(_tableTag: Tag) extends profile.api.Table[GeneKeggRow](_tableTag, Some("mardb"), "gene_kegg") {
    def * = (geneId, ko, description) <> (GeneKeggRow.tupled, GeneKeggRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(geneId), Rep.Some(ko), Rep.Some(description)).shaped.<>({r=>import r._; _1.map(_=> GeneKeggRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column gene_id SqlType(VARCHAR), Length(255,true) */
    val geneId: Rep[String] = column[String]("gene_id", O.Length(255,varying=true))
    /** Database column ko SqlType(VARCHAR), Length(255,true) */
    val ko: Rep[String] = column[String]("ko", O.Length(255,varying=true))
    /** Database column description SqlType(TEXT) */
    val description: Rep[String] = column[String]("description")

    /** Primary key of GeneKegg (database name gene_kegg_PK) */
    val pk = primaryKey("gene_kegg_PK", (geneId, ko))
  }
  /** Collection-like TableQuery object for table GeneKegg */
  lazy val GeneKegg = new TableQuery(tag => new GeneKegg(tag))

  /** Entity class storing rows of table Go
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param species Database column species SqlType(VARCHAR), Length(255,true)
   *  @param term Database column term SqlType(VARCHAR), Length(255,true)
   *  @param number Database column number SqlType(INT)
   *  @param description Database column description SqlType(TEXT)
   *  @param genes Database column genes SqlType(TEXT)
   *  @param kind Database column kind SqlType(VARCHAR), Length(255,true) */
  case class GoRow(id: Int, species: String, term: String, number: Int, description: String, genes: String, kind: String)
  /** GetResult implicit for fetching GoRow objects using plain SQL queries */
  implicit def GetResultGoRow(implicit e0: GR[Int], e1: GR[String]): GR[GoRow] = GR{
    prs => import prs._
    GoRow.tupled((<<[Int], <<[String], <<[String], <<[Int], <<[String], <<[String], <<[String]))
  }
  /** Table description of table go. Objects of this class serve as prototypes for rows in queries. */
  class Go(_tableTag: Tag) extends profile.api.Table[GoRow](_tableTag, Some("mardb"), "go") {
    def * = (id, species, term, number, description, genes, kind) <> (GoRow.tupled, GoRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(species), Rep.Some(term), Rep.Some(number), Rep.Some(description), Rep.Some(genes), Rep.Some(kind)).shaped.<>({r=>import r._; _1.map(_=> GoRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column species SqlType(VARCHAR), Length(255,true) */
    val species: Rep[String] = column[String]("species", O.Length(255,varying=true))
    /** Database column term SqlType(VARCHAR), Length(255,true) */
    val term: Rep[String] = column[String]("term", O.Length(255,varying=true))
    /** Database column number SqlType(INT) */
    val number: Rep[Int] = column[Int]("number")
    /** Database column description SqlType(TEXT) */
    val description: Rep[String] = column[String]("description")
    /** Database column genes SqlType(TEXT) */
    val genes: Rep[String] = column[String]("genes")
    /** Database column kind SqlType(VARCHAR), Length(255,true) */
    val kind: Rep[String] = column[String]("kind", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Go */
  lazy val Go = new TableQuery(tag => new Go(tag))

  /** Entity class storing rows of table HiberList
   *  @param geneId Database column gene_id SqlType(VARCHAR), PrimaryKey, Length(255,true) */
  case class HiberListRow(geneId: String)
  /** GetResult implicit for fetching HiberListRow objects using plain SQL queries */
  implicit def GetResultHiberListRow(implicit e0: GR[String]): GR[HiberListRow] = GR{
    prs => import prs._
    HiberListRow(<<[String])
  }
  /** Table description of table hiber_list. Objects of this class serve as prototypes for rows in queries. */
  class HiberList(_tableTag: Tag) extends profile.api.Table[HiberListRow](_tableTag, Some("mardb"), "hiber_list") {
    def * = geneId <> (HiberListRow, HiberListRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = Rep.Some(geneId).shaped.<>(r => r.map(_=> HiberListRow(r.get)), (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column gene_id SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val geneId: Rep[String] = column[String]("gene_id", O.PrimaryKey, O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table HiberList */
  lazy val HiberList = new TableQuery(tag => new HiberList(tag))

  /** Entity class storing rows of table HibernationDiffGene
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param species Database column species SqlType(VARCHAR), Length(255,true)
   *  @param platform Database column platform SqlType(VARCHAR), Length(255,true)
   *  @param tissue Database column tissue SqlType(VARCHAR), Length(255,true)
   *  @param geneId Database column gene_id SqlType(VARCHAR), Length(255,true)
   *  @param geneSymbol Database column gene_symbol SqlType(VARCHAR), Length(255,true)
   *  @param log2fc Database column log2Fc SqlType(DOUBLE)
   *  @param pvalue Database column pValue SqlType(VARCHAR), Length(255,true)
   *  @param chr Database column chr SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param start Database column start SqlType(INT), Default(None)
   *  @param end Database column end SqlType(INT), Default(None) */
  case class HibernationDiffGeneRow(id: Int, species: String, platform: String, tissue: String, geneId: String, geneSymbol: String, log2fc: Double, pvalue: String, chr: Option[String] = None, start: Option[Int] = None, end: Option[Int] = None)
  /** GetResult implicit for fetching HibernationDiffGeneRow objects using plain SQL queries */
  implicit def GetResultHibernationDiffGeneRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Double], e3: GR[Option[String]], e4: GR[Option[Int]]): GR[HibernationDiffGeneRow] = GR{
    prs => import prs._
    HibernationDiffGeneRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[Double], <<[String], <<?[String], <<?[Int], <<?[Int]))
  }
  /** Table description of table hibernation_diff_gene. Objects of this class serve as prototypes for rows in queries. */
  class HibernationDiffGene(_tableTag: Tag) extends profile.api.Table[HibernationDiffGeneRow](_tableTag, Some("mardb"), "hibernation_diff_gene") {
    def * = (id, species, platform, tissue, geneId, geneSymbol, log2fc, pvalue, chr, start, end) <> (HibernationDiffGeneRow.tupled, HibernationDiffGeneRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(species), Rep.Some(platform), Rep.Some(tissue), Rep.Some(geneId), Rep.Some(geneSymbol), Rep.Some(log2fc), Rep.Some(pvalue), chr, start, end).shaped.<>({r=>import r._; _1.map(_=> HibernationDiffGeneRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9, _10, _11)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column species SqlType(VARCHAR), Length(255,true) */
    val species: Rep[String] = column[String]("species", O.Length(255,varying=true))
    /** Database column platform SqlType(VARCHAR), Length(255,true) */
    val platform: Rep[String] = column[String]("platform", O.Length(255,varying=true))
    /** Database column tissue SqlType(VARCHAR), Length(255,true) */
    val tissue: Rep[String] = column[String]("tissue", O.Length(255,varying=true))
    /** Database column gene_id SqlType(VARCHAR), Length(255,true) */
    val geneId: Rep[String] = column[String]("gene_id", O.Length(255,varying=true))
    /** Database column gene_symbol SqlType(VARCHAR), Length(255,true) */
    val geneSymbol: Rep[String] = column[String]("gene_symbol", O.Length(255,varying=true))
    /** Database column log2Fc SqlType(DOUBLE) */
    val log2fc: Rep[Double] = column[Double]("log2Fc")
    /** Database column pValue SqlType(VARCHAR), Length(255,true) */
    val pvalue: Rep[String] = column[String]("pValue", O.Length(255,varying=true))
    /** Database column chr SqlType(VARCHAR), Length(255,true), Default(None) */
    val chr: Rep[Option[String]] = column[Option[String]]("chr", O.Length(255,varying=true), O.Default(None))
    /** Database column start SqlType(INT), Default(None) */
    val start: Rep[Option[Int]] = column[Option[Int]]("start", O.Default(None))
    /** Database column end SqlType(INT), Default(None) */
    val end: Rep[Option[Int]] = column[Option[Int]]("end", O.Default(None))
  }
  /** Collection-like TableQuery object for table HibernationDiffGene */
  lazy val HibernationDiffGene = new TableQuery(tag => new HibernationDiffGene(tag))

  /** Entity class storing rows of table Kegg
   *  @param id Database column id SqlType(VARCHAR), Length(255,true)
   *  @param kind Database column kind SqlType(VARCHAR), Length(255,true)
   *  @param pathway Database column pathway SqlType(VARCHAR), Length(255,true)
   *  @param geneNumber Database column gene_number SqlType(INT)
   *  @param geneId Database column gene_id SqlType(TEXT) */
  case class KeggRow(id: String, kind: String, pathway: String, geneNumber: Int, geneId: String)
  /** GetResult implicit for fetching KeggRow objects using plain SQL queries */
  implicit def GetResultKeggRow(implicit e0: GR[String], e1: GR[Int]): GR[KeggRow] = GR{
    prs => import prs._
    KeggRow.tupled((<<[String], <<[String], <<[String], <<[Int], <<[String]))
  }
  /** Table description of table kegg. Objects of this class serve as prototypes for rows in queries. */
  class Kegg(_tableTag: Tag) extends profile.api.Table[KeggRow](_tableTag, Some("mardb"), "kegg") {
    def * = (id, kind, pathway, geneNumber, geneId) <> (KeggRow.tupled, KeggRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(kind), Rep.Some(pathway), Rep.Some(geneNumber), Rep.Some(geneId)).shaped.<>({r=>import r._; _1.map(_=> KeggRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(VARCHAR), Length(255,true) */
    val id: Rep[String] = column[String]("id", O.Length(255,varying=true))
    /** Database column kind SqlType(VARCHAR), Length(255,true) */
    val kind: Rep[String] = column[String]("kind", O.Length(255,varying=true))
    /** Database column pathway SqlType(VARCHAR), Length(255,true) */
    val pathway: Rep[String] = column[String]("pathway", O.Length(255,varying=true))
    /** Database column gene_number SqlType(INT) */
    val geneNumber: Rep[Int] = column[Int]("gene_number")
    /** Database column gene_id SqlType(TEXT) */
    val geneId: Rep[String] = column[String]("gene_id")

    /** Primary key of Kegg (database name kegg_PK) */
    val pk = primaryKey("kegg_PK", (id, kind))
  }
  /** Collection-like TableQuery object for table Kegg */
  lazy val Kegg = new TableQuery(tag => new Kegg(tag))

  /** Entity class storing rows of table Mode
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param test Database column test SqlType(VARCHAR), Length(255,true) */
  case class ModeRow(id: Int, test: String)
  /** GetResult implicit for fetching ModeRow objects using plain SQL queries */
  implicit def GetResultModeRow(implicit e0: GR[Int], e1: GR[String]): GR[ModeRow] = GR{
    prs => import prs._
    ModeRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table mode. Objects of this class serve as prototypes for rows in queries. */
  class Mode(_tableTag: Tag) extends profile.api.Table[ModeRow](_tableTag, Some("mardb"), "mode") {
    def * = (id, test) <> (ModeRow.tupled, ModeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(test)).shaped.<>({r=>import r._; _1.map(_=> ModeRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column test SqlType(VARCHAR), Length(255,true) */
    val test: Rep[String] = column[String]("test", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Mode */
  lazy val Mode = new TableQuery(tag => new Mode(tag))

  /** Entity class storing rows of table Orthologs
   *  @param geneId Database column gene_id SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param human Database column human SqlType(VARCHAR), Length(255,true)
   *  @param mouse Database column mouse SqlType(VARCHAR), Length(255,true)
   *  @param rat Database column rat SqlType(VARCHAR), Length(255,true)
   *  @param hamster Database column hamster SqlType(VARCHAR), Length(255,true)
   *  @param blackBear Database column black_bear SqlType(VARCHAR), Length(255,true)
   *  @param squirrel Database column squirrel SqlType(VARCHAR), Length(255,true)
   *  @param bat Database column bat SqlType(VARCHAR), Length(255,true) */
  case class OrthologsRow(geneId: String, human: String, mouse: String, rat: String, hamster: String, blackBear: String, squirrel: String, bat: String)
  /** GetResult implicit for fetching OrthologsRow objects using plain SQL queries */
  implicit def GetResultOrthologsRow(implicit e0: GR[String]): GR[OrthologsRow] = GR{
    prs => import prs._
    OrthologsRow.tupled((<<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table orthologs. Objects of this class serve as prototypes for rows in queries. */
  class Orthologs(_tableTag: Tag) extends profile.api.Table[OrthologsRow](_tableTag, Some("mardb"), "orthologs") {
    def * = (geneId, human, mouse, rat, hamster, blackBear, squirrel, bat) <> (OrthologsRow.tupled, OrthologsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(geneId), Rep.Some(human), Rep.Some(mouse), Rep.Some(rat), Rep.Some(hamster), Rep.Some(blackBear), Rep.Some(squirrel), Rep.Some(bat)).shaped.<>({r=>import r._; _1.map(_=> OrthologsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column gene_id SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val geneId: Rep[String] = column[String]("gene_id", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column human SqlType(VARCHAR), Length(255,true) */
    val human: Rep[String] = column[String]("human", O.Length(255,varying=true))
    /** Database column mouse SqlType(VARCHAR), Length(255,true) */
    val mouse: Rep[String] = column[String]("mouse", O.Length(255,varying=true))
    /** Database column rat SqlType(VARCHAR), Length(255,true) */
    val rat: Rep[String] = column[String]("rat", O.Length(255,varying=true))
    /** Database column hamster SqlType(VARCHAR), Length(255,true) */
    val hamster: Rep[String] = column[String]("hamster", O.Length(255,varying=true))
    /** Database column black_bear SqlType(VARCHAR), Length(255,true) */
    val blackBear: Rep[String] = column[String]("black_bear", O.Length(255,varying=true))
    /** Database column squirrel SqlType(VARCHAR), Length(255,true) */
    val squirrel: Rep[String] = column[String]("squirrel", O.Length(255,varying=true))
    /** Database column bat SqlType(VARCHAR), Length(255,true) */
    val bat: Rep[String] = column[String]("bat", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Orthologs */
  lazy val Orthologs = new TableQuery(tag => new Orthologs(tag))

  /** Entity class storing rows of table OtherOrthologs
   *  @param himalayan Database column himalayan SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param yellow Database column yellow SqlType(VARCHAR), Length(255,true)
   *  @param alpine Database column alpine SqlType(VARCHAR), Length(255,true) */
  case class OtherOrthologsRow(himalayan: String, yellow: String, alpine: String)
  /** GetResult implicit for fetching OtherOrthologsRow objects using plain SQL queries */
  implicit def GetResultOtherOrthologsRow(implicit e0: GR[String]): GR[OtherOrthologsRow] = GR{
    prs => import prs._
    OtherOrthologsRow.tupled((<<[String], <<[String], <<[String]))
  }
  /** Table description of table other_orthologs. Objects of this class serve as prototypes for rows in queries. */
  class OtherOrthologs(_tableTag: Tag) extends profile.api.Table[OtherOrthologsRow](_tableTag, Some("mardb"), "other_orthologs") {
    def * = (himalayan, yellow, alpine) <> (OtherOrthologsRow.tupled, OtherOrthologsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(himalayan), Rep.Some(yellow), Rep.Some(alpine)).shaped.<>({r=>import r._; _1.map(_=> OtherOrthologsRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column himalayan SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val himalayan: Rep[String] = column[String]("himalayan", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column yellow SqlType(VARCHAR), Length(255,true) */
    val yellow: Rep[String] = column[String]("yellow", O.Length(255,varying=true))
    /** Database column alpine SqlType(VARCHAR), Length(255,true) */
    val alpine: Rep[String] = column[String]("alpine", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table OtherOrthologs */
  lazy val OtherOrthologs = new TableQuery(tag => new OtherOrthologs(tag))

  /** Entity class storing rows of table Pep
   *  @param geneid Database column geneId SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param pep Database column pep SqlType(TEXT) */
  case class PepRow(geneid: String, pep: String)
  /** GetResult implicit for fetching PepRow objects using plain SQL queries */
  implicit def GetResultPepRow(implicit e0: GR[String]): GR[PepRow] = GR{
    prs => import prs._
    PepRow.tupled((<<[String], <<[String]))
  }
  /** Table description of table pep. Objects of this class serve as prototypes for rows in queries. */
  class Pep(_tableTag: Tag) extends profile.api.Table[PepRow](_tableTag, Some("mardb"), "pep") {
    def * = (geneid, pep) <> (PepRow.tupled, PepRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(geneid), Rep.Some(pep)).shaped.<>({r=>import r._; _1.map(_=> PepRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column geneId SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val geneid: Rep[String] = column[String]("geneId", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column pep SqlType(TEXT) */
    val pep: Rep[String] = column[String]("pep")
  }
  /** Collection-like TableQuery object for table Pep */
  lazy val Pep = new TableQuery(tag => new Pep(tag))

  /** Entity class storing rows of table PlagueList
   *  @param geneId Database column gene_id SqlType(VARCHAR), PrimaryKey, Length(255,true) */
  case class PlagueListRow(geneId: String)
  /** GetResult implicit for fetching PlagueListRow objects using plain SQL queries */
  implicit def GetResultPlagueListRow(implicit e0: GR[String]): GR[PlagueListRow] = GR{
    prs => import prs._
    PlagueListRow(<<[String])
  }
  /** Table description of table plague_list. Objects of this class serve as prototypes for rows in queries. */
  class PlagueList(_tableTag: Tag) extends profile.api.Table[PlagueListRow](_tableTag, Some("mardb"), "plague_list") {
    def * = geneId <> (PlagueListRow, PlagueListRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = Rep.Some(geneId).shaped.<>(r => r.map(_=> PlagueListRow(r.get)), (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column gene_id SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val geneId: Rep[String] = column[String]("gene_id", O.PrimaryKey, O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table PlagueList */
  lazy val PlagueList = new TableQuery(tag => new PlagueList(tag))
}

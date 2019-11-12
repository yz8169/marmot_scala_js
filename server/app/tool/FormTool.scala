package tool

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.format.Formats.doubleFormat
import tool.Pojo.SpeciesData


/**
  * Created by yz on 2018/7/25
  */

case class SubmissionData(name: String, email: String, subject: String, message: String)

case class HDGSearchData(chr: Option[String], start: Option[Int], end: Option[Int], min: Option[Double], max: Option[Double],
                         tissue: Option[String], platform: Option[String], species: Option[String])

class FormTool {

  case class FileNameData(fileName: String)

  val fileNameForm = Form(
    mapping(
      "fileName" -> text
    )(FileNameData.apply)(FileNameData.unapply)
  )

  case class GoData(kinds: Seq[String],species:Seq[String])

  val goForm = Form(
    mapping(
      "kinds" -> seq(text),
      "species" -> seq(text)
    )(GoData.apply)(GoData.unapply)
  )

  case class KindsData(kinds: Seq[String])

  val kindsForm = Form(
    mapping(
      "kinds" -> seq(text)
    )(KindsData.apply)(KindsData.unapply)
  )

  case class GroupData(geneId: String, groupName: String)

  val groupForm = Form(
    mapping(
      "geneId" -> text,
      "groupName" -> text
    )(GroupData.apply)(GroupData.unapply)
  )

  case class ProjectNameData(projectName: String)

  val projectNameForm = Form(
    mapping(
      "projectName" -> text
    )(ProjectNameData.apply)(ProjectNameData.unapply)
  )

  case class ExpV1Data(projectName: String, tissue: String)

  val expV1Form = Form(
    mapping(
      "projectName" -> text,
      "tissue" -> text
    )(ExpV1Data.apply)(ExpV1Data.unapply)
  )


  case class ExpData(projectName: String, ud: String, min: Option[Double], max: Option[Double],
                     pMin: Option[Double], pMax: Option[Double], comparisons: Seq[String]
                    )

  val expForm = Form(
    mapping(
      "projectName" -> text,
      "ud" -> text,
      "min" -> optional(of(doubleFormat)),
      "max" -> optional(of(doubleFormat)),
      "pMin" -> optional(of(doubleFormat)),
      "pMax" -> optional(of(doubleFormat)),
      "comparisons" -> seq(text)
    )(ExpData.apply)(ExpData.unapply)
  )

  case class KeggData(species: Seq[String])

  val keggForm = Form(
    mapping(
      "species" -> seq(text)
    )(KeggData.apply)(KeggData.unapply)
  )

  case class KeggEnrichData(geneId: Option[String], database: String, method: String, fdr: String, cutoff: String, pValue: String)

  val keggEnrichForm = Form(
    mapping(
      "geneId" -> optional(text),
      "database" -> text,
      "method" -> text,
      "fdr" -> text,
      "cutoff" -> text,
      "pValue" -> text
    )(KeggEnrichData.apply)(KeggEnrichData.unapply)
  )

  case class MuscleData(queryText: Option[String], tree: String)

  val muscleForm = Form(
    mapping(
      "queryText" -> optional(text),
      "tree" -> text
    )(MuscleData.apply)(MuscleData.unapply)
  )

  case class QueryData(queryText: Option[String], evalue: String, wordSize: String, maxTargetSeqs: String)

  val queryForm = Form(
    mapping(
      "queryText" -> optional(text),
      "evalue" -> text,
      "wordSize" -> text,
      "maxTargetSeqs" -> text
    )(QueryData.apply)(QueryData.unapply)
  )

  case class BlastData(queryText: Option[String], againstType: String, evalue: String, wordSize: String, maxTargetSeqs: String)

  val blastForm = Form(
    mapping(
      "queryText" -> optional(text),
      "againstType" -> text,
      "evalue" -> text,
      "wordSize" -> text,
      "maxTargetSeqs" -> text
    )(BlastData.apply)(BlastData.unapply)
  )

  val submitForm = Form(
    mapping(
      "name" -> text,
      "email" -> text,
      "subject" -> text,
      "message" -> text
    )(SubmissionData.apply)(SubmissionData.unapply)
  )

  case class MummerData(targetText: Option[String], queryText: Option[String])

  val mummerForm = Form(
    mapping(
      "targetText" -> optional(text),
      "queryText" -> optional(text)
    )(MummerData.apply)(MummerData.unapply)
  )

  case class GeneIdData(geneId: String)

  val geneIdForm = Form(
    mapping(
      "geneId" -> text
    )(GeneIdData.apply)(GeneIdData.unapply)
  )

  val speciesForm = Form(
    mapping(
      "species" -> text
    )(SpeciesData.apply)(SpeciesData.unapply)
  )

  case class BlockIdData(blockId: String)

  val blockIdForm = Form(
    mapping(
      "blockId" -> text
    )(BlockIdData.apply)(BlockIdData.unapply)
  )

  case class KeywordData(keyword: String)

  val keywordForm = Form(
    mapping(
      "keyword" -> text
    )(KeywordData.apply)(KeywordData.unapply)
  )

  val hDGSearchForm = Form(
    mapping(
      "chr" -> optional(text),
      "start" -> optional(number),
      "end" -> optional(number),
      "min" -> optional(of(doubleFormat)),
      "max" -> optional(of(doubleFormat)),
      "tissue" -> optional(text),
      "platform" -> optional(text),
      "species" -> optional(text)
    )(HDGSearchData.apply)(HDGSearchData.unapply)
  )


}

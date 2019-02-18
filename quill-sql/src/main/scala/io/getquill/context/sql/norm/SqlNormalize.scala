package io.getquill.context.sql.norm

import io.getquill.norm._
import io.getquill.ast.Ast
import io.getquill.norm.ConcatBehavior.AnsiConcat
import io.getquill.util.Messages.trace

object SqlNormalize {
  def apply(ast: Ast, concatBehavior: ConcatBehavior = AnsiConcat) =
    new SqlNormalize(concatBehavior)(ast)
}

class SqlNormalize(concatBehavior: ConcatBehavior) {

  private val normalize =
    (identity[Ast] _)
      .andThen(trace("original"))
      .andThen(new FlattenOptionOperation(concatBehavior).apply _)
      .andThen(trace("FlattenOptionOperation"))
      .andThen(SimplifyNullChecks.apply _)
      .andThen(trace("SimplifyNullChecks"))
      .andThen(Normalize.apply _)
      .andThen(trace("Normalize"))
      .andThen(RenameProperties.apply _)
      .andThen(trace("RenameProperties"))
      .andThen(ExpandDistinct.apply _)
      .andThen(trace("ExpandDistinct"))
      .andThen(ExpandJoin.apply _)
      .andThen(trace("ExpandJoin"))
      .andThen(ExpandMappedInfix.apply _)
      .andThen(trace("ExpandMappedInfix"))
      .andThen(Normalize.apply _)
      .andThen(trace("Normalize"))

  def apply(ast: Ast) = normalize(ast)
}

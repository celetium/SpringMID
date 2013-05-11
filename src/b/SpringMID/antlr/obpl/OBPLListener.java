// Generated from OBPL.g4 by ANTLR 4.0
package b.SpringMID.antlr.obpl;
import org.antlr.v4.runtime.tree.ParseTreeListener;

public interface OBPLListener extends ParseTreeListener {
	void enterExpression(OBPLParser.ExpressionContext ctx);
	void exitExpression(OBPLParser.ExpressionContext ctx);

	void enterItemAttribute(OBPLParser.ItemAttributeContext ctx);
	void exitItemAttribute(OBPLParser.ItemAttributeContext ctx);

	void enterCompilationUnit(OBPLParser.CompilationUnitContext ctx);
	void exitCompilationUnit(OBPLParser.CompilationUnitContext ctx);

	void enterExpressionList(OBPLParser.ExpressionListContext ctx);
	void exitExpressionList(OBPLParser.ExpressionListContext ctx);

	void enterUnaryExpression(OBPLParser.UnaryExpressionContext ctx);
	void exitUnaryExpression(OBPLParser.UnaryExpressionContext ctx);

	void enterCompareExpression(OBPLParser.CompareExpressionContext ctx);
	void exitCompareExpression(OBPLParser.CompareExpressionContext ctx);

	void enterBoDeclaration(OBPLParser.BoDeclarationContext ctx);
	void exitBoDeclaration(OBPLParser.BoDeclarationContext ctx);

	void enterOrExpression(OBPLParser.OrExpressionContext ctx);
	void exitOrExpression(OBPLParser.OrExpressionContext ctx);

	void enterTypeDeclaration(OBPLParser.TypeDeclarationContext ctx);
	void exitTypeDeclaration(OBPLParser.TypeDeclarationContext ctx);

	void enterKeyDeclaration(OBPLParser.KeyDeclarationContext ctx);
	void exitKeyDeclaration(OBPLParser.KeyDeclarationContext ctx);

	void enterAdditiveOp(OBPLParser.AdditiveOpContext ctx);
	void exitAdditiveOp(OBPLParser.AdditiveOpContext ctx);

	void enterPckRefed(OBPLParser.PckRefedContext ctx);
	void exitPckRefed(OBPLParser.PckRefedContext ctx);

	void enterAndExpression(OBPLParser.AndExpressionContext ctx);
	void exitAndExpression(OBPLParser.AndExpressionContext ctx);

	void enterBoolExpression(OBPLParser.BoolExpressionContext ctx);
	void exitBoolExpression(OBPLParser.BoolExpressionContext ctx);

	void enterIdAbsPath(OBPLParser.IdAbsPathContext ctx);
	void exitIdAbsPath(OBPLParser.IdAbsPathContext ctx);

	void enterMultiplicativeOp(OBPLParser.MultiplicativeOpContext ctx);
	void exitMultiplicativeOp(OBPLParser.MultiplicativeOpContext ctx);

	void enterPrimary(OBPLParser.PrimaryContext ctx);
	void exitPrimary(OBPLParser.PrimaryContext ctx);

	void enterFieldDeclaration(OBPLParser.FieldDeclarationContext ctx);
	void exitFieldDeclaration(OBPLParser.FieldDeclarationContext ctx);

	void enterIdentifierSuffix(OBPLParser.IdentifierSuffixContext ctx);
	void exitIdentifierSuffix(OBPLParser.IdentifierSuffixContext ctx);

	void enterImportDeclaration(OBPLParser.ImportDeclarationContext ctx);
	void exitImportDeclaration(OBPLParser.ImportDeclarationContext ctx);

	void enterPackageDeclaration(OBPLParser.PackageDeclarationContext ctx);
	void exitPackageDeclaration(OBPLParser.PackageDeclarationContext ctx);

	void enterCaseStatement(OBPLParser.CaseStatementContext ctx);
	void exitCaseStatement(OBPLParser.CaseStatementContext ctx);

	void enterLoopStatement(OBPLParser.LoopStatementContext ctx);
	void exitLoopStatement(OBPLParser.LoopStatementContext ctx);

	void enterQualifiedName(OBPLParser.QualifiedNameContext ctx);
	void exitQualifiedName(OBPLParser.QualifiedNameContext ctx);

	void enterNotExpression(OBPLParser.NotExpressionContext ctx);
	void exitNotExpression(OBPLParser.NotExpressionContext ctx);

	void enterExpressionWithAssign(OBPLParser.ExpressionWithAssignContext ctx);
	void exitExpressionWithAssign(OBPLParser.ExpressionWithAssignContext ctx);

	void enterAdditiveExpression(OBPLParser.AdditiveExpressionContext ctx);
	void exitAdditiveExpression(OBPLParser.AdditiveExpressionContext ctx);

	void enterPckItemAttributes(OBPLParser.PckItemAttributesContext ctx);
	void exitPckItemAttributes(OBPLParser.PckItemAttributesContext ctx);

	void enterBpDeclaration(OBPLParser.BpDeclarationContext ctx);
	void exitBpDeclaration(OBPLParser.BpDeclarationContext ctx);

	void enterStatement(OBPLParser.StatementContext ctx);
	void exitStatement(OBPLParser.StatementContext ctx);

	void enterIdEnumeration(OBPLParser.IdEnumerationContext ctx);
	void exitIdEnumeration(OBPLParser.IdEnumerationContext ctx);

	void enterArguments(OBPLParser.ArgumentsContext ctx);
	void exitArguments(OBPLParser.ArgumentsContext ctx);

	void enterCompareOp(OBPLParser.CompareOpContext ctx);
	void exitCompareOp(OBPLParser.CompareOpContext ctx);

	void enterRefDeclaration(OBPLParser.RefDeclarationContext ctx);
	void exitRefDeclaration(OBPLParser.RefDeclarationContext ctx);

	void enterFieldDefinition1(OBPLParser.FieldDefinition1Context ctx);
	void exitFieldDefinition1(OBPLParser.FieldDefinition1Context ctx);

	void enterIdWithIdentifier(OBPLParser.IdWithIdentifierContext ctx);
	void exitIdWithIdentifier(OBPLParser.IdWithIdentifierContext ctx);

	void enterPckDeclaration(OBPLParser.PckDeclarationContext ctx);
	void exitPckDeclaration(OBPLParser.PckDeclarationContext ctx);

	void enterFieldDefinition2(OBPLParser.FieldDefinition2Context ctx);
	void exitFieldDefinition2(OBPLParser.FieldDefinition2Context ctx);

	void enterNetDeclaration(OBPLParser.NetDeclarationContext ctx);
	void exitNetDeclaration(OBPLParser.NetDeclarationContext ctx);

	void enterBoBody(OBPLParser.BoBodyContext ctx);
	void exitBoBody(OBPLParser.BoBodyContext ctx);

	void enterFieldInfer(OBPLParser.FieldInferContext ctx);
	void exitFieldInfer(OBPLParser.FieldInferContext ctx);

	void enterBpBody(OBPLParser.BpBodyContext ctx);
	void exitBpBody(OBPLParser.BpBodyContext ctx);

	void enterLiteral(OBPLParser.LiteralContext ctx);
	void exitLiteral(OBPLParser.LiteralContext ctx);

	void enterMultiplicativeExpression(OBPLParser.MultiplicativeExpressionContext ctx);
	void exitMultiplicativeExpression(OBPLParser.MultiplicativeExpressionContext ctx);
}
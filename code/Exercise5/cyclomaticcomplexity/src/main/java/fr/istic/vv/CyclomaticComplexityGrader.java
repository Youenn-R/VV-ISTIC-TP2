package fr.istic.vv;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

import java.io.FileWriter;
import java.io.IOException;

public class CyclomaticComplexityGrader extends VoidVisitorWithDefaults<Void> {
    private int arretes = 0;
    private int noeuds = 0;
    private String pack = "[No Package]";
    private String classe = "[No Class]";

    // permet de créer notre fichier résultat
    private final FileWriter writer;

    public CyclomaticComplexityGrader() throws IOException {
        writer = new FileWriter("result.csv");
        // entêtes 
        writer.write("Package,Class,Method,Parameters,Cyclomatic Complexity\n");
    }

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        if (unit.getPackageDeclaration().isPresent()) {
            pack = unit.getPackageDeclaration().get().getNameAsString();
        }
        for (TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
    }

    private void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        if (!declaration.isPublic()) return;
        classe = declaration.getNameAsString();
        for (MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
        }

        for (BodyDeclaration<?> member : declaration.getMembers()) {
            if (member instanceof TypeDeclaration) {
                member.accept(this, arg);
            }
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(EnumDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        // On remet à jour pour chaque fonction
        arretes = 0;
        noeuds = 1;

        if (declaration.getBody().isPresent()) {
            declaration.getBody().get().accept(this, arg);
        }

        // On récupère le nom de la méthode et ses paramètres
        String methodName = declaration.getNameAsString();
        // on formate pour éviter les bugs csv du aux virgules
        String parameters = declaration.getParameters().toString().replace(',', ' ');

        // Calcul de la complexité cyclomatique : CC = E - N + 2P
        int cc = arretes - noeuds + 2;

        try {
            writer.write(String.format("%s,%s,%s,%s,%d\n", pack, classe, methodName, parameters, cc));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(IfStmt stmt, Void arg) {
        arretes++;
        noeuds++;
        super.visit(stmt, arg);
    }

    @Override
    public void visit(ForStmt stmt, Void arg) {
        arretes += 2;
        noeuds++;
        super.visit(stmt, arg);
    }

    @Override
    public void visit(WhileStmt stmt, Void arg) {
        arretes += 2;
        noeuds++;
        super.visit(stmt, arg);
    }

    @Override
    public void visit(DoStmt stmt, Void arg) {
        arretes += 2;
        noeuds++;
        super.visit(stmt, arg);
    }

    @Override
    public void visit(SwitchStmt stmt, Void arg) {
        int cases = stmt.getEntries().size();
        arretes += cases;
        noeuds += cases;
        super.visit(stmt, arg);
    }

    @Override
    public void visit(CatchClause stmt, Void arg) {
        arretes++;
        noeuds++;
        super.visit(stmt, arg);
    }

    @Override
    public void visit(BinaryExpr expr, Void arg) {
        if (expr.getOperator() == BinaryExpr.Operator.AND || expr.getOperator() == BinaryExpr.Operator.OR) {
            arretes++;
            noeuds++;
        }
        super.visit(expr, arg);
    }

    @Override
    public void visit(BlockStmt n, Void arg) {
        noeuds++;
        for (Statement stmt : n.getStatements()) {
            stmt.accept(this, arg);
        }
    }

    @Override
    public void visit(ExpressionStmt n, Void arg) {
        n.getExpression().accept(this, arg);
    }

    @Override
    public void visit(AssignExpr n, Void arg) {
        arretes++;
        noeuds++;
        super.visit(n, arg);
    }

    public void closeWriter() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la fermeture du fichier", e);
        }
    }
}

# Code of your exercise

    package fr.istic.vv;
    
    import com.github.javaparser.ast.CompilationUnit;
    import com.github.javaparser.ast.body.*;
    import com.github.javaparser.ast.nodeTypes.NodeWithName;
    import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
    
    import java.io.BufferedWriter;
    import java.io.FileWriter;
    import java.io.IOException;
    
    
    public class PrivateFieldPrinter extends VoidVisitorWithDefaults<Void> {
    
        private BufferedWriter writer;
    
        public PrivateFieldPrinter(String outputFilePath) throws IOException {
            writer = new BufferedWriter(new FileWriter(outputFilePath));
            writer.write("Field Name; Declaring Class; Package");
            writer.newLine();
        }
    
    
        @Override
        public void visit(CompilationUnit unit, Void arg) {
            for(TypeDeclaration<?> type : unit.getTypes()) {
                type.accept(this, null);
            }
        }
    
        public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
            if(!declaration.isPublic()) return;
            declaration.getFields().stream()
                    .filter(field -> !field.isPublic())
                    .forEach(field -> fieldParser(field,declaration, arg));
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
            if(!declaration.isPublic()) return;
            System.out.println(" " + declaration.getDeclarationAsString(true, true));
        }
    
        @Override
        public void visit(VariableDeclarator var, Void arg) {
            String declaringClass = var.findAncestor(ClassOrInterfaceDeclaration.class)
                            .map(ClassOrInterfaceDeclaration::getNameAsString)
                            .orElse("[Anonymous]");
            String classPackage = var.findAncestor(CompilationUnit.class)
                            .flatMap(CompilationUnit::getPackageDeclaration)
                            .map(NodeWithName::getNameAsString)
                            .orElse("[Package]");
            try {
                writer.write(var.getNameAsString() + ";" + declaringClass+ ";" + classPackage);
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    
        private String majuscule(String str) {
            if (str == null || str.isEmpty()) {
                return str;
            }
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    
        private void fieldParser(FieldDeclaration field, TypeDeclaration<?> declaration, Void arg){
            for(VariableDeclarator var : field.getVariables()){
                String getterName = "get" + majuscule(var.getNameAsString());
                if (declaration.getMethods().stream().anyMatch(m -> m.getNameAsString().equals(getterName))) {
                    var.accept(this, arg);
                }
            }
        }
    
        public void closeWriter() {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            System.err.println("Should provide the path to the source code");
            System.exit(1);
        }

        File file = new File(args[0]);
        if(!file.exists() || !file.isDirectory() || !file.canRead()) {
            System.err.println("Provide a path to an existing readable directory");
            System.exit(2);
        }

        SourceRoot root = new SourceRoot(file.toPath());
        PrivateFieldPrinter printer = new PrivateFieldPrinter("code/Exercise4/rapport.csv");
        root.parse("", (localPath, absolutePath, result) -> {
            result.ifSuccessful(unit -> unit.accept(printer, null));
            return SourceRoot.Callback.Result.DONT_SAVE;
        });
        printer.closeWriter();
    }

package InsertCommentCommand;

import java.util.ArrayList;
import java.util.List;

public class GenerateComment {
    private String indent;
    private InfoForComment ifc;
    private List<String> comment = new ArrayList<>();;

    public List<String> getComment() {
        return comment;
    }

    public GenerateComment(String indent, InfoForComment ifc){
        this.indent = indent;
        this.ifc = ifc;

        startGeneratingComment();
    }

    private void startGeneratingComment(){
        switch (ifc.commentType){
            case BlockComment:
                createBlockComment();
                break;
            case InlineComment:
                createInlineComment();
                break;
            case ClassComment:
                createClassComment();
                break;
            case FunctionComment:
                createFunctionComment();
                break;
            case SameLineComment:
                createInlineComment();
                break;
            case ConstructorComment:
                createConstructorComment();
                break;
        }
    }

    private void createFunctionComment(){
        comment.add(indent+"/**");
        comment.add(indent+" * SCCA Comment");
        comment.add(indent+" * @brief");
        if (ifc.params != null) {
            for (String s: ifc.params) {
                comment.add(indent + " * @param " + s);
            }
        }
        if (ifc.returnContent != null) {
            comment.add(indent + " * @return " + ifc.returnContent);
        }
        comment.add(indent+" */");
    }

    private void createConstructorComment(){
        comment.add(indent+"/**");
        comment.add(indent+" * コンストラクタ");
        comment.add(indent+" */");
    }

    private void createBlockComment(){
        comment.add(indent+"/**");
        comment.add(indent+" * SCCA Comment");
        comment.add(indent+" */");
    }

    private void createInlineComment(){
        comment.add(indent+"//! SCCA Comment");
    }

    private void createClassComment(){
        comment.add(indent+"/**");
        comment.add(indent+" * SCCA Comment");
        comment.add(indent+" */");
    }
}

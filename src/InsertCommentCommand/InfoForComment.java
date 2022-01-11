package InsertCommentCommand;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;

public class InfoForComment {
    public ParserRuleContext ctx;
    public boolean needsDecision;
    public CommentType commentType;
    public int lineNum;

    public String summary;
    public List<String> params = new ArrayList<>();
    public String returnContent;

    public InfoForComment(ParserRuleContext ctx){
        this.ctx = ctx;
    }

    public InfoForComment(int lineNum, CommentType commentType){
        this.lineNum = lineNum;
        this.commentType = commentType;
    }

    public InfoForComment(int lineNum, CommentType commentType, List<String> params){
        this.lineNum = lineNum;
        this.commentType = commentType;
        this.params = params;
    }

    public InfoForComment(ParserRuleContext ctx, boolean b, CommentType ct){
        this.ctx = ctx;
        this.lineNum = ctx.getStart().getLine();
        this.needsDecision = b;
        this.commentType = ct;
    }

    public InfoForComment(ParserRuleContext ctx, boolean b, CommentType ct, List<String> params){
        this.ctx = ctx;
        this.lineNum = ctx.getStart().getLine();
        this.needsDecision = b;
        this.commentType = ct;
        this.params = params;
    }

    public int getLineNum() {
        return lineNum;
    }
}
package ParserOfNeedCommand.Listener;

import File.FileInputer;
import File.InfoForNecessaryComments;
import InsertCommentCommand.CommentType;
import InsertCommentCommand.InfoForComment;
import ParserOfNeedCommand.Generated.CPP14BaseListener;
import ParserOfNeedCommand.Generated.CPP14Parser;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CommentsListener extends CPP14BaseListener {
    private CommonTokenStream tokens;
    private CPP14Parser parser;

    private ArrayList<String> results = new ArrayList<String>();
    public List<String> getResults() {
        return this.results;
    }

    private String sccaComment = "SCCA Comment";

    private boolean isFunctiondefinition = false;
    private boolean isParametersandqualifiers = false;
    private boolean isEnumspecifier = false;
    private boolean isParameterdeclaration = false;
    private String classType = null;
    private String classname = null;
    private String functionname = null;
    private String nestedClassType = null;

    public List<String> params = new ArrayList<>();

    private InfoForNecessaryComments infoObj;
    //以前のコメントのTokenIndex
    int previusComments = -100;

    public CommentsListener(CommonTokenStream tokens, CPP14Parser parser) {
        this.tokens = tokens;
        this.parser = parser;
        //TODO 設定ファイルをオプションで選択できるようにした方がいい？
        FileInputer fileInPuter = new FileInputer();
        fileInPuter.readSettingFileForNecessaryComments("Config/SettingForNecessaryComments.json");
        infoObj = fileInPuter.getInfoForNecessaryCommentsObj();
    }

    private List<InfoForComment> Ifcs = new ArrayList<InfoForComment>();
    private List<InfoForComment> ResultIfcs = new ArrayList<InfoForComment>();
    public List<InfoForComment> getResultIfcs() {
        return ResultIfcs;
    }

    //    //自作された型のポインタ
//    @Override
//    public void enterExpressionstatement(CPP14Parser.ExpressionstatementContext ctx){
//        determineWhetherCommentIsNecessary(ctx);
//    }

    //関数定義、メンバ関数定義の前
    @Override
    public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
        isFunctiondefinition = true;
        functionname = null;
        params = new ArrayList<>();
    }

    @Override
    public void exitFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
        if (classType == null) {
            if (Objects.equals(classname, functionname)){
                Ifcs.add(new InfoForComment(ctx, infoObj.isConstructor(), CommentType.FunctionComment, params));
                classname = null;
            }else {
                Ifcs.add(new InfoForComment(ctx, infoObj.isFunction(), CommentType.FunctionComment, params));
//            System.out.println("関数" + ctx.getStart().getLine());
            }
        } else {
            if (Objects.equals(classname, functionname)){
                Ifcs.add(new InfoForComment(ctx, infoObj.isConstructor(), CommentType.FunctionComment, params));
//                System.out.println("コンストラクタ" + ctx.getStart().getLine());
            } else {
                Ifcs.add(new InfoForComment(ctx, infoObj.isMemberFunction(), CommentType.FunctionComment, params));
//                System.out.println("メンバ関数" + ctx.getStart().getLine());
            }
        }
        isFunctiondefinition = false;
    }

    //(メンバ?)関数宣言，変数宣言の前．
    @Override
    public void enterMemberdeclaration(CPP14Parser.MemberdeclarationContext ctx) {
        //TODO memberdeclartionで，simpletypespecifierになってるやつが変数名になる？
        isEnumspecifier = false;
    }

    @Override
    public void exitMemberdeclaration(CPP14Parser.MemberdeclarationContext ctx) {
//        System.out.println(ctx.getChildCount());
        if (ctx.getChildCount() == 1) {
            //メンバ関数
//            if (Objects.equals(classname, functionname)){
//                System.out.println("コンストラクタ" + ctx.getStart().getLine());
//            } else {
//                Ifcs.add(new InfoForComment(ctx, infoObj.isClassMemberFunction(), CommentType.FunctionComment));
//                System.out.println("メンバ関数" + ctx.getStart().getLine());
//            }

        } else if (ctx.getChildCount() == 2){
            //入れ子クラス

            if (isEnumspecifier){
                //列挙体
                Ifcs.add(new InfoForComment(ctx, infoObj.isEnumStatement(), CommentType.BlockComment));
//            System.out.println("enum" + ctx.getStart().getLine());
                isEnumspecifier = false;
            }
        }else if (ctx.getChildCount() == 3){
            if(Objects.equals(classType, "class")) {
                //クラスのメンバ変数
                Ifcs.add(new InfoForComment(ctx, infoObj.isClassMemberVariables(), CommentType.InlineComment));
                //                System.out.println("classメンバ変数" + ctx.getStart().getLine());
            } else if (Objects.equals(classType, "struct")) {
                //構造体のメンバ変数
                Ifcs.add(new InfoForComment(ctx, infoObj.isStructMemberVariables(), CommentType.SameLineComment));
                //            System.out.println("structメンバ変数" + ctx.getStart().getLine());
            } else if (Objects.equals(classType, "union")) {
                //共用体のメンバ変数
                Ifcs.add(new InfoForComment(ctx, infoObj.isUnionMemberVariables(), CommentType.SameLineComment));
                //            System.out.println("unionメンバ変数" + ctx.getStart().getLine());
            }
        } else {
            //その他メンバ??
            Ifcs.add(new InfoForComment(ctx));
//            System.out.println("その他メンバ" + ctx.getStart().getLine());
        }
    }

    //ParametersandqualifiersでMemberdeclarationが関数宣言？
    @Override
    public void enterParametersandqualifiers(CPP14Parser.ParametersandqualifiersContext ctx) {
        isParametersandqualifiers = true;
    }

    //変数宣言、クラス,構造体の前
    @Override
    public void enterSimpledeclaration(CPP14Parser.SimpledeclarationContext ctx) {
        isEnumspecifier = false;
    }

    @Override
    public void enterClasskey(CPP14Parser.ClasskeyContext ctx) {
        if(classType == null) {
            classType = ctx.getText();
        }
    }

    @Override public void enterClassname(CPP14Parser.ClassnameContext ctx) {
        if (classname == null){
            classname = ctx.getText();
        }
    }
    @Override public void enterUnqualifiedid(CPP14Parser.UnqualifiedidContext ctx) {
        if (isFunctiondefinition) {
            if (functionname == null) {
                functionname = ctx.getText();
            }
            if (isParameterdeclaration){
                this.params.add(ctx.getText());
            }
        }
    }

    @Override
    public void enterEnumspecifier(CPP14Parser.EnumspecifierContext ctx) {
        isEnumspecifier = true;
    }

    @Override public void exitClassspecifier(CPP14Parser.ClassspecifierContext ctx) {
        //class
    }

    //引数
    @Override public void enterParameterdeclaration(CPP14Parser.ParameterdeclarationContext ctx) {
        isParameterdeclaration = true;
    }
    @Override public void exitParameterdeclaration(CPP14Parser.ParameterdeclarationContext ctx) {
        isParameterdeclaration = false;
    }
    @Override public void enterDeclspecifierseq(CPP14Parser.DeclspecifierseqContext ctx) {
        //kata
    }

    @Override
    public void exitSimpledeclaration(CPP14Parser.SimpledeclarationContext ctx) {
//        for (int i = 0; i < ctx.getChildCount(); i++){
//            System.out.println("ko" + ctx.getChild(i).getText());
//        }
//        System.out.println("ko:" + ctx.getChild(1).getText());
        if (ctx.getChildCount() == 2){
            //class
            if (isEnumspecifier) {
                //列挙体
                Ifcs.add(new InfoForComment(ctx, infoObj.isEnumStatement(), CommentType.BlockComment));
                isEnumspecifier = false;
//            System.out.println("enum" + ctx.getStart().getLine());
            }
            if (Objects.equals(classType, "class")) {
                //クラス
                Ifcs.add(new InfoForComment(ctx, infoObj.isClass(), CommentType.ClassComment));
//                System.out.println("クラス" + ctx.getStart().getLine());
            } else if (Objects.equals(classType, "struct")) {
                //構造体
                Ifcs.add(new InfoForComment(ctx, infoObj.isStructure(), CommentType.BlockComment));
//                System.out.println("構造体" + ctx.getStart().getLine());
            } else if (Objects.equals(classType, "union")) {
                //共用体
                Ifcs.add(new InfoForComment(ctx, infoObj.isUnion(), CommentType.BlockComment));
//                System.out.println("共用体" + ctx.getStart().getLine());
            } else {
                Ifcs.add(new InfoForComment(ctx));
            }
        }else if (ctx.getChildCount() == 3){
            //hensuu
            if (isFunctiondefinition) {
                //関数内の変数
                Ifcs.add(new InfoForComment(ctx, infoObj.isFunctionVariables(), CommentType.InlineComment));
//            System.out.println("関数内の変数" + ctx.getStart().getLine());
            }else {
                //その他
                Ifcs.add(new InfoForComment(ctx, infoObj.isGlobalVariables(), CommentType.InlineComment));
                System.out.println("グローバル変数" + ctx.getStart().getLine());
            }
        }
    }

    @Override public void exitDeclaration(CPP14Parser.DeclarationContext ctx) {
        classType = null;
        classname = null;
    }

    //Loopの前
    @Override
    public void enterIterationstatement(CPP14Parser.IterationstatementContext ctx) {
        //Iterationstatementの最も左側のTokenを取得
        Ifcs.add(new InfoForComment(ctx, infoObj.isIterationsStatement(), CommentType.InlineComment));
    }

    //if,switchの前
    @Override
    public void enterSelectionstatement(CPP14Parser.SelectionstatementContext ctx) {
        Ifcs.add(new InfoForComment(ctx, infoObj.isSelectionsStatement(), CommentType.InlineComment));
    }

    @Override public void enterTranslationunit(CPP14Parser.TranslationunitContext ctx) {
        List<Token> beforeCommentChannel = getBeforeHiddenTokens(ctx, 2);
        //ファイルへのコメントがある場合
    }

    @Override
    public void exitTranslationunit(CPP14Parser.TranslationunitContext ctx) {
        Ifcs.sort(Comparator.comparing(InfoForComment::getLineNum));
        for (InfoForComment ifc : Ifcs) {
            if (ifc.needsDecision) {
                determineWhetherCommentIsNecessary(ifc);
            } else {
                determinePreviusComments(ifc.ctx);
            }
        }
        for (String result : results) {
            System.out.println(result);
        }
    }

    //ここがもっとも重要なコード
    //設定がtrueのとき
    private void determineWhetherCommentIsNecessary(InfoForComment ifc) {
        ParserRuleContext ctx = ifc.ctx;
        Token startToken = ctx.getStart();
        Token stopToken = ctx.getStop();
        List<Token> beforeCommentChannel = getBeforeHiddenTokens(ctx, 2);
        List<Token> afterCommentChannel = getAfterHiddenTokens(ctx, 2);

        int beforeIndex = 0;
        int afterIndex = 0;

        if (beforeCommentChannel != null) {
            beforeIndex = beforeCommentChannel.size() -1 ;
            String strBeforeComment = beforeCommentChannel.get(beforeIndex).toString();
            if (strBeforeComment.contains(sccaComment)) {
                outPutWhereSccaComments(startToken);
                return;
            }
        }

        //TODO 条件分岐が複雑すぎる．真理値表を参照．

        if (beforeCommentChannel == null && afterCommentChannel == null) {
            outPutWhereNeedToComments(ifc);
        } else if (beforeCommentChannel == null && afterCommentChannel != null) {
            //後にあるコメントがステートメントと同じ行にある．
            if (afterCommentChannel.get(afterIndex).getLine() != stopToken.getLine()) {
                outPutWhereNeedToComments(ifc);
            } else {
                previusComments = afterCommentChannel.get(afterIndex).getTokenIndex();
            }
        } else if (beforeCommentChannel != null && afterCommentChannel == null) {
            //保持しているコメントと，以前のコメントが一緒じゃない．
            if ((previusComments != beforeCommentChannel.get(beforeIndex).getTokenIndex())) {
            } else {
                outPutWhereNeedToComments(ifc);
            }
        } else {
            if (afterCommentChannel.get(afterIndex).getLine() != stopToken.getLine()) {
                if (previusComments != beforeCommentChannel.get(beforeIndex).getTokenIndex()) {
                } else {
                    outPutWhereNeedToComments(ifc);
                }
            } else {
                previusComments = afterCommentChannel.get(afterIndex).getTokenIndex();
            }
        }
    }

    //設定がfalseのとき
    private void determinePreviusComments(ParserRuleContext ctx) {
        Token startToken = ctx.getStart();
        Token stopToken = ctx.getStop();
        List<Token> beforeCommentChannel = getBeforeHiddenTokens(ctx, 2);
        List<Token> afterCommentChannel = getAfterHiddenTokens(ctx, 2);

        int beforeIndex = 0;
        int afterIndex = 0;

        if (beforeCommentChannel != null) {
            beforeIndex = beforeCommentChannel.size() -1 ;
            String strBeforeComment = beforeCommentChannel.get(beforeIndex).toString();
            if (strBeforeComment.contains(sccaComment)) {
                outPutWhereSccaComments(startToken);
                return;
            }
        }

        if (beforeCommentChannel == null && afterCommentChannel == null) {
        } else if (beforeCommentChannel == null && afterCommentChannel != null) {
            //後にあるコメントがステートメントと同じ行にある．
            if (afterCommentChannel.get(afterIndex).getLine() != stopToken.getLine()) {
            } else {
                previusComments = afterCommentChannel.get(afterIndex).getTokenIndex();
            }
        } else if (beforeCommentChannel != null && afterCommentChannel == null) {
            //保持しているコメントと，以前のコメントが一緒じゃない．
            if ((previusComments != beforeCommentChannel.get(beforeIndex).getTokenIndex())) {
            } else {
            }
        } else {
            if (afterCommentChannel.get(afterIndex).getLine() != stopToken.getLine()) {
                if (previusComments != beforeCommentChannel.get(beforeIndex).getTokenIndex()) {
                } else {
                }
            } else {
                previusComments = afterCommentChannel.get(afterIndex).getTokenIndex();
            }
        }
    }

    //隠れているトークンを取得
    private List<Token> getBeforeHiddenTokens(ParserRuleContext ctx, int type) {
        Token token = ctx.getStart();
        int i = token.getTokenIndex();
        //TODO 前にある複数のコメントを取りたい．今は一つだけしか取れない．なぜ
        List<Token> CommentChannel = this.tokens.getHiddenTokensToLeft(i, type);
        return CommentChannel;
    }

    //隠れているトークンを取得
    private List<Token> getAfterHiddenTokens(ParserRuleContext ctx, int type) {
        Token token = ctx.getStop();
        int i = token.getTokenIndex();
        //TODO 前にある複数のコメントを取りたい．今は一つだけしか取れない．なぜ
        List<Token> CommentChannel = this.tokens.getHiddenTokensToRight(i, type);
        return CommentChannel;
    }

    //コメントの出力の処理
    private void outPutWhereNeedToComments(InfoForComment ifc) {
        Token token = ifc.ctx.getStart();
        String msg = token.getLine() + "行目の " + token.getText() + "の前にコメントが必要です";

        //重複があるときは追加しないようにするための処理
        if (results.isEmpty()) {
            this.results.add(msg);
            if (ifc.params == null) {
                ResultIfcs.add(new InfoForComment(token.getLine(), ifc.commentType));
            } else {
                ResultIfcs.add(new InfoForComment(token.getLine(), ifc.commentType, ifc.params));
            }
        } else if (msg.equals(results.get(results.size() - 1))) { //msgと，resultsの最後の要素が一致してるなら，何もしない．

        } else {
            this.results.add(msg);
            if (ifc.params == null) {
                ResultIfcs.add(new InfoForComment(token.getLine(), ifc.commentType));
            } else {
                ResultIfcs.add(new InfoForComment(token.getLine(), ifc.commentType, ifc.params));
            }
        }
    }

    //SCCAが作成したコメントの場合の処理
    private void outPutWhereSccaComments(Token token) {
        String msg = token.getLine() + "行目の " + token.getText() + "の前のコメントは修正が必要です";

        //重複があるときは追加しないようにするための処理
        if (results.isEmpty()) {
            this.results.add(msg);
        } else if (msg.equals(results.get(results.size() - 1))) { //msgと，resultsの最後の要素が一致してるなら，何もしない．

        } else {
            this.results.add(msg);
        }
    }
}
const TaskGameCore = (data) => {
    var self = {};

    var init = (initData) => {

    }
    //game
    self.getVariant = (variantString, task) => {
        variantString = JSON.parse(JSON.stringify(variantString));
        task = JSON.parse(JSON.stringify(task));

        var variantObject;

        //wybieranie odpowiedniej logiki dla konkretnego template'a
        switch (variantString) {
            case "WordFill":
                variantObject = WordFill_Game(task);
            break;
            case "WordConnect":
                variantObject = WordConnect_Game(task);
            break;
            case "ChronologicalOrder":
                variantObject = ChronologicalOrder_Game(task);
            break;
            case "ListWordFill":
                variantObject = ListWordFill_Game(task);
            break;
            case "ListSentenceFormingAnswer":
                variantObject = ListSentenceForming_Game(task);
            break;
            case "ListChoiceWordFill": 
                //TODO: not finished!!!
                //variantObject = ListSentenceForming_Game(task);
            //break;
            default:
                console.warn("To pole jest tylko dla jeszcze nie zaimplementowancyh tasków, w produkcji nie powinno się nigdy wykonać!");
                variantObject = {
                    taskDoesNotExist : true,
                    getAnswers: () => { 
                        console.log("hello ListWordFill");
                        //ListWordFill answers... więc nie zadziała dla np ListChoiceWordFill?
                        return {answers: [["test"]]} 
                    }
                };
            break;
        }

        return variantObject;
    }

    init(data);
    return self;
}
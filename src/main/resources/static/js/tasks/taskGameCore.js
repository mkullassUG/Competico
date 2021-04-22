const TaskGameCore = (data) => {
    var self = {};
    self.resizeVariantObserver;

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
            case "ListSentenceForming":
                //TODO: not finished!!!
                variantObject = ListSentenceForming_Game(task);
            break;
            case "ListChoiceWordFill":
                //TODO: not finished!!!
                variantObject = ListChoiceWordFill_Game(task);
            break;
            default:
                console.warn("To pole jest tylko dla jeszcze nie zaimplementowancyh tasków, w produkcji nie powinno się nigdy wykonać!");
                
            break;
        }

        if (variantObject)
            self.setupResizeVariantObserver(variantObject);

        return variantObject;
    }

    self.setupResizeVariantObserver = (variantObject) => {
        //informuje wariant gry o zmianie szerokości gamediv'a i pomaga poustawiać elementy od nowa
        if (self.resizeVariantObserver)
            self.resizeVariantObserver.unobserve(document.querySelector("#GameDiv"));

        self.resizeVariantObserver = new ResizeObserver(function(entries) {
            
            if (typeof variantObject.taskDoesNotExist === 'undefined') 
                variantObject.ResizeObserverVariantFunction();
                
        });
  
        self.resizeVariantObserver.observe(document.querySelector("#GameDiv"));
    }

    init(data);
    return self;
}
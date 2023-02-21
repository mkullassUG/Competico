const TaskGameCore = (data) => {

    if (TaskGameCore.singleton)
        return TaskGameCore.singleton;
    var self = {};
    if (!TaskGameCore.singleton)
        TaskGameCore.singleton = self;

    self.resizeVariantObserver;
    self.variantObject;

    self.tablicaPolskichNazwTaskow = {
        'WordFill': 
        'Test\n <br> <small>Cloze test</small>',

        'WordConnect': 
        'Połącz w pary\n <br> <small>Łączenie linią</small>',

        'ChronologicalOrder': 
        'Porządkowanie chronologiczne',

        'ListWordFill':
        "Test\n <br> <small>Wybierz słowo</small>",

        "ListChoiceWordFill":
        "Test\n <br> <small>Wybierz właściwą opcję</small>",

        'ListSentenceForming' : 
        "Układanie zdań z podanych wyrazów",

        'OptionSelect': 
        "Quiz",
    };

    var init = (initData_) => {}
    
    self.getVariant = (variantString, task, instruction) => {

        if ( !variantString || !task)
            if ( self.variantObject )
                return self.variantObject
            else {
                console.warn("To nie powinno się wydarzyć!")
                return false;
            }

        variantString = JSON.parse(JSON.stringify(variantString));
        task = JSON.parse(JSON.stringify(task));
        self.variantObject = {};
        
        $("#gameInstruction").html(``);
        $("#gameInstruction").append(document.createTextNode(instruction));
        
        switch (variantString) {
            case "WordFill":
                self.variantObject = WordFill_Game(task);
            break;
            case "WordConnect":
                self.variantObject = WordConnect_Game(task);
            break;
            case "ChronologicalOrder":
                self.variantObject = ChronologicalOrder_Game(task);
            break;
            case "ListWordFill":
                self.variantObject = ListWordFill_Game(task);
            break;
            case "ListSentenceForming":
                self.variantObject = ListSentenceForming_Game(task);
            break;
            case "ListChoiceWordFill":
                self.variantObject = ListChoiceWordFill_Game(task);
            break;
            case "OptionSelect":
                self.variantObject = OptionSelect_Game(task);
            break;
            default:
                console.warn("This is only for unimplemented variants and should never fire in production.");
                
            break;
        }

        if (self.variantObject)
            self.setupResizeVariantObserver(self.variantObject);

        return self.variantObject;
    }

    self.setupResizeVariantObserver = (variantObject) => {

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

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {TaskGameCore};

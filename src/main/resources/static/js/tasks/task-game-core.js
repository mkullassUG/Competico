const TaskGameCore = (data) => {

    if (TaskGameCore.singleton)
        return TaskGameCore.singleton;
    var self = {};
    if (!TaskGameCore.singleton)
        TaskGameCore.singleton = self;


    self.resizeVariantObserver;
    self.variantObject;

    /* 
    Wypełnianie luk w tekście - test/Quiz
    jeden wielozdaniowy tekst, jedna  pula odpowiedzi - cloze test
    oddzielne zdania ułożone w wierszach -brakujące słowo (przeciągnij i upuść brakujące słowo w zdaniu)
    łączenie słów i zwrotów... - połącz w pary 
    układanie zdań - porządkowanie - przeciągaj i upuszczaj słowa aby ułożyć z nich zdania

    Oddzielne zdania ułożone w wierszach, jedna pula odpowiedzi na wiersz
    wybierz słowo

    Oddzielne zdania ułożone w wierszach, wybór słowa dla każdej luki" - wybierz właściwą opcję
    

    */
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

    var init = (initData) => {

    }
    
    //game
    self.getVariant = (variantString, task, instruction) => {

        //jeśli niepodano nazwy i obiektu taska to wysyłam obecny variant
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
        
        //wybieranie odpowiedniej logiki dla konkretnego template'a
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
                //TODO: not finished!!!
                self.variantObject = OptionSelect_Game(task);
            break;
            default:
                console.warn("To pole jest tylko dla jeszcze nie zaimplementowancyh tasków, w produkcji nie powinno się nigdy wykonać!");
                
            break;
        }

        if (self.variantObject)
            self.setupResizeVariantObserver(self.variantObject);

        return self.variantObject;
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

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {TaskGameCore};

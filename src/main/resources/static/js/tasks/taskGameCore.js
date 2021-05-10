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
        'Wypełnianie luk w tekście\n <br> <small>Jeden wielozdaniowy tekst, jedna pula odpowiedzi</small>',

        'WordConnect': 
        'Łączenie słów i zwrotów z dwóch kolumn,\n <br> <small>Łączenie linią</small>',

        'WordDrag': 
        'Łączenie słów i zwrotów z dwóch kolumn,\n <br> <small>Dopasowanie drugiej kolumny z podanego zestawu</small>',

        'WordWrite': 
        'Łączenie słów i zwrotów z dwóch kolumn,\n <br> <small>Dopasowanie drugiej kolumny przez ręczne wpisanie odpowedzi</small>',

        'ChronologicalOrder': 
        'Układanie zdań w porządek chronologiczny',

        'ListWordFill':
        "Wypełnianie luk w tekście\n <br> <small>Oddzielne zdania ułożone w wierszach, jedna pula odpowiedzi na wiersz</small>",

        "ListChoiceWordFill":
        "Wypełnianie luk w tekście\n <br> <small>Oddzielne zdania ułożone w wierszach, wybór słowa dla każdej luki</small>",

        'ListSentenceForming' : 
        "Układanie zdań z podanych wyrazów.",

        'OptionSelect': 
        "Zaznaczanie poprawnych odpowiedzi (Kahoot)",

        'PictureToManyWords': 
        "Jeden obrazek i wiele odpowiedzi. <br> <small>Warianty z obrazkami.</small>",

        'SentenceToManyPictures': 
        "Jeden tekst i wiele obrazków. <br> <small>Warianty z obrazkami.</small>",

        'ManyPicturesManyWords': 
        "Wiele obrazków i wiele słów. <br> <small>Warianty z obrazkami.</small>",

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
            // case "SingleChoice":
            //     //TODO: not finished!!!
            //     self.variantObject = SingleChoice_Game(task);
            // break;
            case "OptionSelect":
                //TODO: not finished!!!
                self.variantObject = OptionSelect_Game(task);
            break;
            case "SentenceToManyPictures":
                //TODO: not finished!!!
                self.variantObject = SentenceToManyPictures_Game(task);
            break;
            case "PictureToManyWords":
                //TODO: not finished!!!
                self.variantObject = PictureToManyWords_Game(task);
            break;
            case "ManyPicturesManyWords":
                //TODO: not finished!!!
                self.variantObject = ManyPicturesManyWords_Game(task);
            break;
            case "WordDrag":
                //TODO: not finished!!!
                self.variantObject = WordDrag_Game(task);
            break;
            case "WordConnectAnswerType":
                //TODO: not finished!!!
                self.variantObject = WordConnectAnswerType_Game(task);
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

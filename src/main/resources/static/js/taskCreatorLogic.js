const TaskCreatorLogic = (playerInfo_, debug) => {
    var self = {};
    self.playerInfo = playerInfo_;
    self.debug = debug;
    self.currentVariant;
    /*       logic variables          */

    /*       logic functions          */
    self.taskCreatorInit = () => {
        /* TODO
        przygotuj start strony
        */
       self.changeVariant("WordFIll");
    }

    self.changeVariant = (variantString) => {
        /*TODO:
        -sprawdzanie czy wpisano coś w pola obecnego wariantu
            -jesli tak to zapytanie czy na pewno...    
        -zmiana wariantu do edytowania na przycisk
        */

        /*warianty:
            1 - Wypełnianie luk w tekście   (WordFIll)
                1.2 - Jeden wielozdaniowy tekst, jedna pula odpowiedzi
            2 - Łączenie słów i zwrotów z dwóch kolumn (WordConnect)
            3 - Układanie zdań w porządek chronologiczny (ChronologicalOrder)
        */

        //sprawdzanie
        switch (variantString) {
            case "WordFIll":
                self.currentVarian = WordFillCreator();
                break;
            case "ChronologicalOrder":
                self.currentVarian = ChronologicalOrderCreator();
                break;
            case "WordConnect":
                self.currentVarian = WordConnectCreator();
                break;
           default:
               console.warn("TODO, to nie powinno się wydarzyć!")
               break;
        }

    }

    /*       event listeners          */
    if ($("#btnChronologicalOrder").length)
        $("#btnChronologicalOrder").on("click",()=>{
            if (self.debug)
                console.log("btnChronologicalOrder");
            self.changeVariant("ChronologicalOrder");
        });
    if ($("#btnWordFIll").length)
        $("#btnWordFIll").on("click",()=>{
            if (self.debug)
                console.log("btnWordFIll");
            self.changeVariant("WordFIll");
        });
    if ($("#btnWordConnect").length)
        $("#btnWordConnect").on("click",()=>{
            if (self.debug)
                console.log("btnWordConnect");
            self.changeVariant("WordConnect");
        });
        
    /*  initalization  */
    self.taskCreatorInit();
     
    return self;
}

TaskCreatorLogic.getInstance = (debug) => {

    if (TaskCreatorLogic.singleton)
        return TaskCreatorLogic.singleton;

    var ajaxReceiveWhoAmI = ( ) => {
        
        $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/playerinfo",
        contentType: "application/json",
        success: function(playerInfo, textStatus, jqXHR) {
            if (debug){
            console.log("ajaxReceiveWhoAmI success");
            console.log(playerInfo);
            console.log(textStatus);
            console.log(jqXHR);
            }
            TaskCreatorLogic.singleton = TaskCreatorLogic(playerInfo, debug);
            console.log("TaskCreatorLogic");
        },
        error: function(jqXHR, status, err) {
            if (debug){
            console.warn("ajaxReceiveWhoAmI error");
            console.log(data);
            console.log(textStatus);
            console.log(jqXHR);
            }
        }
        });
    }
    
    ajaxReceiveWhoAmI();
    return TaskCreatorLogic.singleton;
}

/* textarea wtf it is blurry fix*/
var observe;
if (window.attachEvent) {
    observe = function (element, event, handler) {
        element.attachEvent('on'+event, handler(element));
    };
}
else {
    observe = function (element, event, handler) {
        element.addEventListener(event, handler(element), false);
    };
}
function textareaAutoscroll () {
    //var text = document.getElementById('wordFillDivTaskText');
    var allText = $(document).find(".taskTextTextarea");
    
    //currying concept https://en.wikipedia.org/wiki/Currying
    var resize = function(text) {
        return function curried_func(e) {
            text.style.height = 'auto';
            text.style.height = text.scrollHeight+'px';
        }
    }

    var delayedresize = function(text) {
        return function curried_func(e) {
            window.setTimeout(resize(text), 0);
        }
    }
    for ( let i = 0; i < allText.length; i++) {
        var text = allText[i];
        
        /* 0-timeout to get the already changed text */
        
        observe(text, 'change',  resize);
        observe(text, 'cut',     delayedresize);
        observe(text, 'paste',   delayedresize);
        observe(text, 'drop',    delayedresize);
        observe(text, 'keydown', delayedresize);
    
        text.focus();
        text.select();
        resize(text);
    }
}
$(document).ready(function(){
    textareaAutoscroll();
});


/*  dropdown menu   */
/*TODO przerobić tak żeby działało jak ja chce przy dynamicznie zmieniającym się ekranie*/
// Prevent closing from click inside dropdown
$(document).on('click', '.dropdown-menu', function (e) {
    e.stopPropagation();
});
  
// make it as accordion for smaller screens
$('.dropdown-menu a').click(function(e){

    if ($(window).width() < 930) { //jak okno jest mniejsze to rozwiń wewnątrz
        e.preventDefault();

        if($(this).next('.submenu').length){
            $(this).next('.submenu').toggle();
        }
        //.one żeby nie zapętlało się niepotrzebnie
        $('.dropdown').one('hide.bs.dropdown', function (e) {
            $(this).find('.submenu').hide();
        })
    } else { //jak okno jest większe to rozwiń na zewnątrz
        if($(this).next('.submenu').length){
            $(this).next('.submenu').toggle();
        }

        $('.dropdown').one('hide.bs.dropdown', function (e) {
            $(this).find('.submenu').hide();
        })
    }
});


/* collapse side-panel*/
/* Set the width of the sidebar to 250px (show it) */
function openNav() {
    document.getElementById("mySidepanel").style.width = "250px";
    // document.getElementById("bigChangeDiv").style.transform = "translateX(250px)"; 
    /*to wtedy zmienie szerokość diva*/
}
  
/* Set the width of the sidebar to 0 (hide it) */
function closeNav() {
    document.getElementById("mySidepanel").style.width = "0";
    // document.getElementById("bigChangeDiv").style.transform = "none"; 
}
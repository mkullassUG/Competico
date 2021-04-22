//ListSentenceForming
const ListSentenceForming_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
    
    /*TODO
      line breaks:
      https://stackoverflow.com/questions/61783668/detect-line-break-with-css

      dodać to do resize observera tego taska
    */
    self.lineBreakDetection_andHrInsertion_In_DivOrderedWords = (DirectParentElmnt) => {
        //elmnt - array słów z jednego bloku do podzielenia na rzędy

        //raczej na początku msuze usuwać wszystkie linie
        DirectParentElmnt.find("hr").remove();

        var elmnt = DirectParentElmnt.find(".LSFword");

        // if ( elmnt.length == 0) {
        //     console.log(DirectParentElmnt.find(".LSFword"))
        //     DirectParentElmnt.prepend(`<li class="LSFword placeholder-empty">blank</li>`);
        // } else {
        //     console.log(DirectParentElmnt.find(".LSFword"))
        //     DirectParentElmnt.find(".placeholder-empty").remove();
        // }

        var previous = null;
        for (var i = 0; i < elmnt.length; i++) {
            var current=elmnt[i];
            //console.log("next");
            var currentRect= current.getBoundingClientRect();
            if(previous!=null) {
                var previousRect= previous.getBoundingClientRect();
                if(currentRect.top!=previousRect.top) {
        
                    //add hr before that element if not yet exists
                    //console.log("adding hr after: ");
                    //console.log(previous);
                    if (!$( current ).prev().is("hr")){
                    //console.log("adding hr")
                    // $( "hr" ).insertBefore( $( current ) );
                        $( "<hr>" ).insertAfter( $( previous ) );
                    }
                    //console.log("was it successful: ");
                    //console.log($( current ).prev().is("hr"));
                } 
            }
            var previous=current;
        }
    
        //last:
        var last = $(elmnt[elmnt.length-1]);
        if (!last.next().is("hr")) {
            $( "<hr>" ).insertAfter( last );
        }
    }
  
    //GameLogic.singleton.setupNewTask({taskName: "ListSentenceFormingAnswer",task:{}})
    //GameLogic.singleton.currentTaskVariant.lineBreakDetection_andHrInsertion_In_DivOrderedWords($(".LSFword "))
    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
    
        //var sentences = 5;

        //ROWS nie WORDS
        var sentence_rows = taskData.words;/*[
            ["Let's", "play", "at", "the", "fword", "park", "you", "wword"],
            ["is", "you", "rword"],
            ["hello!", "my", "name", "is", "SUCC"],
            ["buying", "GF", "1000", "gp"],
            ["Harry", "Poter", "went", "to", "Hogward"]
        ];*/
        var DivListSentenceForming;
        var DivRandomizedWords;
        var DivOrderedWords;
        
        //console.log(taskData);
        $("#GameDiv").html("");
        for ( let i = 0; i < sentence_rows.length; i++) {
            var row = sentence_rows[i];
            //console.log(row)
            DivListSentenceForming = $(`<div class="DivListSentenceForming" id="DivListSentenceForming` + i + `"></div>`);
            DivRandomizedWords = $(`<ul class="DivRandomizedWords border-bottom border-primary connectedSortable`+i+`" id="DivRandomizedWords`+i+`"></ul>`);
            for (let j = 0; j < row.length; j++) {
                var word = row[j];
                //console.log(word)

                DivRandomizedWords.append(`<li class="LSFword">` + word + `</li>`);
            }
    
            //self.lineBreakDetection_andHrInsertion_In_DivOrderedWords(DivRandomizedWords);
            DivOrderedWords = $(`<ul class="DivOrderedWords border-bottom border-primary text-left connectedSortable`+i+`" id="DivOrderedWords`+i+`"></ul>`);
            DivListSentenceForming.append(`<div class="sentenceIndex">` + (i+1) + `</div>`);
            DivListSentenceForming.append(DivRandomizedWords);
            DivListSentenceForming.append(DivOrderedWords);
            
            $("#GameDiv").append(DivListSentenceForming);
            
            function doterHrUpdate ( e, ui ) {

                //może się przydać:
                //https://raw.githubusercontent.com/mattheworiordan/jquery.simulate.drag-sortable.js/master/jquery.simulate.drag-sortable.js

                //$($(".LSFword")[1]).simulateDragSortable({ move: 4 });
                var elmnt = $(e.target).find(".LSFword");

                if ( elmnt.length == 1 && $(e.target).hasClass("DivOrderedWords")) {
                    //fast fix
                    $(elmnt[0]).simulateDragSortable({ move: 1 });
                }

                //console.log("activate")
                //self.lineBreakDetection_andHrInsertion_In_DivOrderedWords($(e.target));
            }

            $( `#DivRandomizedWords` + i + `, #DivOrderedWords`+i ).sortable({
                connectWith: `.connectedSortable`+i,
                placeholder: "ui-state-highlight",
                items: "li:not(.placeholder-empty)",
                cancel: "hr",
                remove: doterHrUpdate,
                update: doterHrUpdate,
                // over: doterHrUpdate,
                // out: doterHrUpdate,
                cursor: "grabbing"
            }).disableSelection();
            
        }
  
    }
  
    var getAnswersSuper = self.getAnswers;
    self.getAnswers = () => {
        var answers = getAnswersSuper();
        answers = [];
        
        var DivOrderedWords = $(".DivOrderedWords");
        for (let i = 0; i < DivOrderedWords.length; i++) {
            var divLSF = DivOrderedWords[i];
            answers[i] = [];
            var wordsDivs = $(divLSF).find(".LSFword");
            for ( let j = 0; j <  wordsDivs.length; j++) {
                var divW = $(wordsDivs[j]);

                answers[i][j] = divW.text();
            }
        }

        //co jeśli array nei będzie wystarczającej długości? uzupełnić nullami?

        var DivRandomizedWords  = $(".DivRandomizedWords ");
        for (let i = 0; i < DivRandomizedWords.length; i++) {
            var divLSF = DivRandomizedWords[i];

            var wordsDivs = $(divLSF).find(".LSFword");
            for ( let j = 0; j <  wordsDivs.length; j++) {
                var divW = $(wordsDivs[j]);

                answers[i].push(divW.text());
            }
        }

        return {"answers": answers};
    }
  
    var resetSuper = self.reset;
    self.reset = () => {
        resetSuper();
    
        self.taskVariantInit(taskData);
    }
    
    self.taskVariantInit(taskData);
    return self;
}
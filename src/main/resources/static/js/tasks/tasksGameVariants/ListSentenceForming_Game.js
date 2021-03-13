//ListSentenceForming
const ListSentenceForming_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
    
    /*TODO
      line breaks:
      https://stackoverflow.com/questions/61783668/detect-line-break-with-css
    */
    self.lineBreakDetection_andHrInsertion_In_DivOrderedWords = (elmnt) => {
        var previous = null;
        for (var i = 0; i < elmnt.length; i++) {
            var current=elmnt[i];
            console.log("next");
            var currentRect= current.getBoundingClientRect();
            if(previous!=null) {
                var previousRect= previous.getBoundingClientRect();
                if(currentRect.top!=previousRect.top) {
        
                    //add hr before that element if not yet exists
                    console.log("adding hr after: ");
                    console.log(previous);
                    if (!$( current ).prev().is("hr")){
                    console.log("adding hr")
                    // $( "hr" ).insertBefore( $( current ) );
                    $( "<hr>" ).insertAfter( $( previous ) );
                    }
                    console.log("was it successful: ");
                    console.log($( current ).prev().is("hr"));
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
  
  
    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
    
        var sentences = 5;
        var sentence_words = [
            ["Let's", "play", "at", "the", "fword", "park", "you", "wword"],
            ["is", "you", "rword"],
            ["hello!", "my", "name", "is", "SUCC"],
            ["buying", "GF", "1000", "gp"],
            ["Harry", "Poter", "went", "to", "Hogward"]
        ];
        var DivListSentenceForming;
        var DivRandomizedWords;
        var DivOrderedWords;
    
        $("#GameDiv").html("");
        for ( let i = 0; i < sentences; i++) {
    
            DivListSentenceForming = $(`<div id="DivListSentenceForming` + i + `"></div>`);
            DivRandomizedWords = $(`<div class="DivRandomizedWords border-bottom border-primary"></div>`);
            var words = sentence_words[i];
            for (let j = 0; j < words.length; j++) {
            var word = words[j];
            DivRandomizedWords.append(`<div class="LSFword">` + word + `</div>`);
            }
    
            DivOrderedWords = $(`<div class="DivOrderedWords border-bottom border-primary"></div>`);
    
            DivListSentenceForming.append(DivRandomizedWords);
            DivListSentenceForming.append(DivOrderedWords);
            
            $("#GameDiv").append(DivListSentenceForming);
        }
  
    }
  
    var getAnswersSuper = self.getAnswers;
    self.getAnswers = () => {
        var answers = getAnswersSuper();
        answers = [];
    
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
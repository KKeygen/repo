/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
$(document).ready(function() {

    $(".click-title").mouseenter( function(    e){
        e.preventDefault();
        this.style.cursor="pointer";
    });
    $(".click-title").mousedown( function(event){
        event.preventDefault();
    });

    // Ugly code while this script is shared among several pages
    try{
        refreshHitsPerSecond(true);
    } catch(e){}
    try{
        refreshResponseTimeOverTime(true);
    } catch(e){}
    try{
        refreshResponseTimePercentiles();
    } catch(e){}
});


var responseTimePercentilesInfos = {
        data: {"result": {"minY": 26.0, "minX": 0.0, "maxY": 867.0, "series": [{"data": [[0.0, 26.0], [0.1, 26.0], [0.2, 42.0], [0.3, 43.0], [0.4, 45.0], [0.5, 46.0], [0.6, 47.0], [0.7, 50.0], [0.8, 50.0], [0.9, 51.0], [1.0, 53.0], [1.1, 53.0], [1.2, 54.0], [1.3, 55.0], [1.4, 57.0], [1.5, 57.0], [1.6, 58.0], [1.7, 58.0], [1.8, 59.0], [1.9, 60.0], [2.0, 64.0], [2.1, 65.0], [2.2, 65.0], [2.3, 68.0], [2.4, 69.0], [2.5, 69.0], [2.6, 69.0], [2.7, 70.0], [2.8, 70.0], [2.9, 78.0], [3.0, 78.0], [3.1, 79.0], [3.2, 81.0], [3.3, 81.0], [3.4, 82.0], [3.5, 83.0], [3.6, 84.0], [3.7, 88.0], [3.8, 91.0], [3.9, 95.0], [4.0, 95.0], [4.1, 95.0], [4.2, 96.0], [4.3, 97.0], [4.4, 98.0], [4.5, 98.0], [4.6, 98.0], [4.7, 101.0], [4.8, 101.0], [4.9, 101.0], [5.0, 102.0], [5.1, 102.0], [5.2, 105.0], [5.3, 105.0], [5.4, 107.0], [5.5, 116.0], [5.6, 123.0], [5.7, 123.0], [5.8, 126.0], [5.9, 128.0], [6.0, 128.0], [6.1, 128.0], [6.2, 128.0], [6.3, 129.0], [6.4, 129.0], [6.5, 131.0], [6.6, 133.0], [6.7, 133.0], [6.8, 133.0], [6.9, 136.0], [7.0, 136.0], [7.1, 138.0], [7.2, 141.0], [7.3, 142.0], [7.4, 143.0], [7.5, 143.0], [7.6, 144.0], [7.7, 144.0], [7.8, 145.0], [7.9, 146.0], [8.0, 151.0], [8.1, 154.0], [8.2, 155.0], [8.3, 155.0], [8.4, 158.0], [8.5, 158.0], [8.6, 159.0], [8.7, 159.0], [8.8, 159.0], [8.9, 166.0], [9.0, 167.0], [9.1, 168.0], [9.2, 168.0], [9.3, 169.0], [9.4, 170.0], [9.5, 170.0], [9.6, 172.0], [9.7, 172.0], [9.8, 173.0], [9.9, 173.0], [10.0, 174.0], [10.1, 175.0], [10.2, 175.0], [10.3, 175.0], [10.4, 176.0], [10.5, 178.0], [10.6, 181.0], [10.7, 183.0], [10.8, 184.0], [10.9, 184.0], [11.0, 184.0], [11.1, 186.0], [11.2, 186.0], [11.3, 187.0], [11.4, 187.0], [11.5, 187.0], [11.6, 193.0], [11.7, 194.0], [11.8, 195.0], [11.9, 195.0], [12.0, 197.0], [12.1, 197.0], [12.2, 199.0], [12.3, 201.0], [12.4, 202.0], [12.5, 207.0], [12.6, 207.0], [12.7, 209.0], [12.8, 210.0], [12.9, 210.0], [13.0, 210.0], [13.1, 210.0], [13.2, 212.0], [13.3, 216.0], [13.4, 224.0], [13.5, 225.0], [13.6, 226.0], [13.7, 226.0], [13.8, 226.0], [13.9, 227.0], [14.0, 227.0], [14.1, 228.0], [14.2, 228.0], [14.3, 237.0], [14.4, 237.0], [14.5, 239.0], [14.6, 240.0], [14.7, 241.0], [14.8, 241.0], [14.9, 242.0], [15.0, 243.0], [15.1, 243.0], [15.2, 258.0], [15.3, 265.0], [15.4, 269.0], [15.5, 270.0], [15.6, 270.0], [15.7, 270.0], [15.8, 270.0], [15.9, 271.0], [16.0, 272.0], [16.1, 279.0], [16.2, 279.0], [16.3, 280.0], [16.4, 280.0], [16.5, 280.0], [16.6, 281.0], [16.7, 281.0], [16.8, 281.0], [16.9, 282.0], [17.0, 282.0], [17.1, 282.0], [17.2, 284.0], [17.3, 284.0], [17.4, 286.0], [17.5, 288.0], [17.6, 288.0], [17.7, 289.0], [17.8, 290.0], [17.9, 291.0], [18.0, 291.0], [18.1, 291.0], [18.2, 292.0], [18.3, 295.0], [18.4, 296.0], [18.5, 296.0], [18.6, 297.0], [18.7, 297.0], [18.8, 297.0], [18.9, 298.0], [19.0, 299.0], [19.1, 300.0], [19.2, 300.0], [19.3, 302.0], [19.4, 303.0], [19.5, 304.0], [19.6, 305.0], [19.7, 306.0], [19.8, 307.0], [19.9, 307.0], [20.0, 308.0], [20.1, 309.0], [20.2, 310.0], [20.3, 313.0], [20.4, 313.0], [20.5, 314.0], [20.6, 315.0], [20.7, 315.0], [20.8, 318.0], [20.9, 320.0], [21.0, 322.0], [21.1, 322.0], [21.2, 323.0], [21.3, 323.0], [21.4, 323.0], [21.5, 323.0], [21.6, 323.0], [21.7, 323.0], [21.8, 324.0], [21.9, 324.0], [22.0, 324.0], [22.1, 325.0], [22.2, 326.0], [22.3, 326.0], [22.4, 326.0], [22.5, 329.0], [22.6, 330.0], [22.7, 332.0], [22.8, 333.0], [22.9, 334.0], [23.0, 335.0], [23.1, 336.0], [23.2, 336.0], [23.3, 336.0], [23.4, 337.0], [23.5, 337.0], [23.6, 337.0], [23.7, 340.0], [23.8, 340.0], [23.9, 341.0], [24.0, 349.0], [24.1, 353.0], [24.2, 353.0], [24.3, 354.0], [24.4, 354.0], [24.5, 354.0], [24.6, 355.0], [24.7, 356.0], [24.8, 356.0], [24.9, 357.0], [25.0, 363.0], [25.1, 363.0], [25.2, 364.0], [25.3, 364.0], [25.4, 365.0], [25.5, 366.0], [25.6, 369.0], [25.7, 373.0], [25.8, 373.0], [25.9, 375.0], [26.0, 376.0], [26.1, 377.0], [26.2, 379.0], [26.3, 381.0], [26.4, 381.0], [26.5, 383.0], [26.6, 383.0], [26.7, 383.0], [26.8, 384.0], [26.9, 384.0], [27.0, 385.0], [27.1, 385.0], [27.2, 386.0], [27.3, 387.0], [27.4, 387.0], [27.5, 387.0], [27.6, 388.0], [27.7, 388.0], [27.8, 388.0], [27.9, 390.0], [28.0, 392.0], [28.1, 394.0], [28.2, 394.0], [28.3, 394.0], [28.4, 396.0], [28.5, 396.0], [28.6, 396.0], [28.7, 397.0], [28.8, 397.0], [28.9, 397.0], [29.0, 397.0], [29.1, 398.0], [29.2, 401.0], [29.3, 402.0], [29.4, 403.0], [29.5, 404.0], [29.6, 407.0], [29.7, 407.0], [29.8, 408.0], [29.9, 408.0], [30.0, 408.0], [30.1, 408.0], [30.2, 408.0], [30.3, 409.0], [30.4, 409.0], [30.5, 409.0], [30.6, 410.0], [30.7, 413.0], [30.8, 414.0], [30.9, 414.0], [31.0, 414.0], [31.1, 415.0], [31.2, 415.0], [31.3, 415.0], [31.4, 416.0], [31.5, 416.0], [31.6, 418.0], [31.7, 420.0], [31.8, 420.0], [31.9, 421.0], [32.0, 422.0], [32.1, 422.0], [32.2, 423.0], [32.3, 424.0], [32.4, 425.0], [32.5, 425.0], [32.6, 427.0], [32.7, 432.0], [32.8, 432.0], [32.9, 435.0], [33.0, 441.0], [33.1, 444.0], [33.2, 446.0], [33.3, 446.0], [33.4, 446.0], [33.5, 448.0], [33.6, 456.0], [33.7, 456.0], [33.8, 457.0], [33.9, 457.0], [34.0, 457.0], [34.1, 462.0], [34.2, 462.0], [34.3, 463.0], [34.4, 464.0], [34.5, 465.0], [34.6, 468.0], [34.7, 472.0], [34.8, 472.0], [34.9, 474.0], [35.0, 474.0], [35.1, 475.0], [35.2, 475.0], [35.3, 475.0], [35.4, 476.0], [35.5, 476.0], [35.6, 483.0], [35.7, 483.0], [35.8, 483.0], [35.9, 487.0], [36.0, 487.0], [36.1, 487.0], [36.2, 489.0], [36.3, 489.0], [36.4, 490.0], [36.5, 491.0], [36.6, 492.0], [36.7, 493.0], [36.8, 495.0], [36.9, 495.0], [37.0, 496.0], [37.1, 496.0], [37.2, 497.0], [37.3, 497.0], [37.4, 498.0], [37.5, 498.0], [37.6, 498.0], [37.7, 499.0], [37.8, 499.0], [37.9, 500.0], [38.0, 500.0], [38.1, 502.0], [38.2, 502.0], [38.3, 502.0], [38.4, 502.0], [38.5, 504.0], [38.6, 504.0], [38.7, 505.0], [38.8, 505.0], [38.9, 506.0], [39.0, 506.0], [39.1, 510.0], [39.2, 511.0], [39.3, 511.0], [39.4, 520.0], [39.5, 521.0], [39.6, 522.0], [39.7, 523.0], [39.8, 523.0], [39.9, 523.0], [40.0, 523.0], [40.1, 523.0], [40.2, 524.0], [40.3, 524.0], [40.4, 524.0], [40.5, 525.0], [40.6, 526.0], [40.7, 526.0], [40.8, 526.0], [40.9, 526.0], [41.0, 527.0], [41.1, 527.0], [41.2, 528.0], [41.3, 528.0], [41.4, 532.0], [41.5, 532.0], [41.6, 533.0], [41.7, 533.0], [41.8, 534.0], [41.9, 534.0], [42.0, 534.0], [42.1, 535.0], [42.2, 536.0], [42.3, 536.0], [42.4, 538.0], [42.5, 539.0], [42.6, 540.0], [42.7, 541.0], [42.8, 542.0], [42.9, 542.0], [43.0, 543.0], [43.1, 544.0], [43.2, 545.0], [43.3, 545.0], [43.4, 546.0], [43.5, 546.0], [43.6, 546.0], [43.7, 547.0], [43.8, 548.0], [43.9, 548.0], [44.0, 549.0], [44.1, 550.0], [44.2, 551.0], [44.3, 551.0], [44.4, 551.0], [44.5, 552.0], [44.6, 552.0], [44.7, 552.0], [44.8, 553.0], [44.9, 553.0], [45.0, 553.0], [45.1, 553.0], [45.2, 557.0], [45.3, 558.0], [45.4, 560.0], [45.5, 562.0], [45.6, 562.0], [45.7, 564.0], [45.8, 565.0], [45.9, 565.0], [46.0, 566.0], [46.1, 566.0], [46.2, 566.0], [46.3, 567.0], [46.4, 569.0], [46.5, 569.0], [46.6, 569.0], [46.7, 570.0], [46.8, 573.0], [46.9, 573.0], [47.0, 573.0], [47.1, 573.0], [47.2, 574.0], [47.3, 574.0], [47.4, 575.0], [47.5, 575.0], [47.6, 576.0], [47.7, 577.0], [47.8, 577.0], [47.9, 577.0], [48.0, 578.0], [48.1, 578.0], [48.2, 578.0], [48.3, 579.0], [48.4, 579.0], [48.5, 580.0], [48.6, 581.0], [48.7, 583.0], [48.8, 583.0], [48.9, 584.0], [49.0, 584.0], [49.1, 584.0], [49.2, 584.0], [49.3, 584.0], [49.4, 587.0], [49.5, 587.0], [49.6, 587.0], [49.7, 587.0], [49.8, 587.0], [49.9, 588.0], [50.0, 589.0], [50.1, 589.0], [50.2, 590.0], [50.3, 590.0], [50.4, 591.0], [50.5, 595.0], [50.6, 595.0], [50.7, 596.0], [50.8, 596.0], [50.9, 596.0], [51.0, 596.0], [51.1, 598.0], [51.2, 598.0], [51.3, 600.0], [51.4, 602.0], [51.5, 614.0], [51.6, 617.0], [51.7, 619.0], [51.8, 620.0], [51.9, 621.0], [52.0, 628.0], [52.1, 634.0], [52.2, 635.0], [52.3, 636.0], [52.4, 637.0], [52.5, 644.0], [52.6, 647.0], [52.7, 647.0], [52.8, 647.0], [52.9, 647.0], [53.0, 648.0], [53.1, 648.0], [53.2, 648.0], [53.3, 649.0], [53.4, 649.0], [53.5, 652.0], [53.6, 652.0], [53.7, 654.0], [53.8, 654.0], [53.9, 654.0], [54.0, 654.0], [54.1, 655.0], [54.2, 657.0], [54.3, 657.0], [54.4, 657.0], [54.5, 658.0], [54.6, 661.0], [54.7, 663.0], [54.8, 664.0], [54.9, 664.0], [55.0, 665.0], [55.1, 666.0], [55.2, 666.0], [55.3, 666.0], [55.4, 666.0], [55.5, 666.0], [55.6, 667.0], [55.7, 667.0], [55.8, 669.0], [55.9, 670.0], [56.0, 670.0], [56.1, 670.0], [56.2, 670.0], [56.3, 671.0], [56.4, 672.0], [56.5, 672.0], [56.6, 672.0], [56.7, 672.0], [56.8, 673.0], [56.9, 674.0], [57.0, 675.0], [57.1, 675.0], [57.2, 675.0], [57.3, 675.0], [57.4, 676.0], [57.5, 676.0], [57.6, 677.0], [57.7, 677.0], [57.8, 678.0], [57.9, 678.0], [58.0, 679.0], [58.1, 680.0], [58.2, 681.0], [58.3, 681.0], [58.4, 681.0], [58.5, 682.0], [58.6, 682.0], [58.7, 682.0], [58.8, 682.0], [58.9, 682.0], [59.0, 683.0], [59.1, 683.0], [59.2, 683.0], [59.3, 683.0], [59.4, 683.0], [59.5, 685.0], [59.6, 685.0], [59.7, 685.0], [59.8, 686.0], [59.9, 686.0], [60.0, 686.0], [60.1, 689.0], [60.2, 689.0], [60.3, 690.0], [60.4, 690.0], [60.5, 691.0], [60.6, 691.0], [60.7, 691.0], [60.8, 691.0], [60.9, 691.0], [61.0, 691.0], [61.1, 692.0], [61.2, 692.0], [61.3, 692.0], [61.4, 692.0], [61.5, 693.0], [61.6, 693.0], [61.7, 693.0], [61.8, 693.0], [61.9, 693.0], [62.0, 694.0], [62.1, 694.0], [62.2, 695.0], [62.3, 696.0], [62.4, 696.0], [62.5, 696.0], [62.6, 699.0], [62.7, 699.0], [62.8, 700.0], [62.9, 700.0], [63.0, 700.0], [63.1, 700.0], [63.2, 700.0], [63.3, 700.0], [63.4, 701.0], [63.5, 701.0], [63.6, 702.0], [63.7, 703.0], [63.8, 706.0], [63.9, 706.0], [64.0, 706.0], [64.1, 708.0], [64.2, 708.0], [64.3, 708.0], [64.4, 710.0], [64.5, 711.0], [64.6, 711.0], [64.7, 712.0], [64.8, 712.0], [64.9, 718.0], [65.0, 719.0], [65.1, 719.0], [65.2, 720.0], [65.3, 720.0], [65.4, 720.0], [65.5, 721.0], [65.6, 721.0], [65.7, 721.0], [65.8, 721.0], [65.9, 721.0], [66.0, 722.0], [66.1, 722.0], [66.2, 722.0], [66.3, 723.0], [66.4, 723.0], [66.5, 723.0], [66.6, 723.0], [66.7, 724.0], [66.8, 725.0], [66.9, 725.0], [67.0, 725.0], [67.1, 726.0], [67.2, 726.0], [67.3, 726.0], [67.4, 726.0], [67.5, 727.0], [67.6, 727.0], [67.7, 727.0], [67.8, 727.0], [67.9, 728.0], [68.0, 728.0], [68.1, 728.0], [68.2, 729.0], [68.3, 729.0], [68.4, 729.0], [68.5, 729.0], [68.6, 729.0], [68.7, 729.0], [68.8, 730.0], [68.9, 730.0], [69.0, 730.0], [69.1, 732.0], [69.2, 734.0], [69.3, 735.0], [69.4, 736.0], [69.5, 736.0], [69.6, 738.0], [69.7, 738.0], [69.8, 739.0], [69.9, 740.0], [70.0, 740.0], [70.1, 741.0], [70.2, 741.0], [70.3, 741.0], [70.4, 743.0], [70.5, 743.0], [70.6, 743.0], [70.7, 743.0], [70.8, 744.0], [70.9, 744.0], [71.0, 745.0], [71.1, 746.0], [71.2, 748.0], [71.3, 749.0], [71.4, 749.0], [71.5, 749.0], [71.6, 751.0], [71.7, 751.0], [71.8, 751.0], [71.9, 752.0], [72.0, 753.0], [72.1, 753.0], [72.2, 753.0], [72.3, 756.0], [72.4, 756.0], [72.5, 758.0], [72.6, 760.0], [72.7, 766.0], [72.8, 771.0], [72.9, 772.0], [73.0, 772.0], [73.1, 773.0], [73.2, 774.0], [73.3, 775.0], [73.4, 775.0], [73.5, 775.0], [73.6, 782.0], [73.7, 782.0], [73.8, 783.0], [73.9, 783.0], [74.0, 784.0], [74.1, 784.0], [74.2, 784.0], [74.3, 785.0], [74.4, 785.0], [74.5, 785.0], [74.6, 786.0], [74.7, 788.0], [74.8, 789.0], [74.9, 790.0], [75.0, 790.0], [75.1, 791.0], [75.2, 792.0], [75.3, 792.0], [75.4, 792.0], [75.5, 793.0], [75.6, 793.0], [75.7, 794.0], [75.8, 794.0], [75.9, 800.0], [76.0, 802.0], [76.1, 803.0], [76.2, 803.0], [76.3, 804.0], [76.4, 804.0], [76.5, 804.0], [76.6, 805.0], [76.7, 805.0], [76.8, 805.0], [76.9, 806.0], [77.0, 807.0], [77.1, 807.0], [77.2, 807.0], [77.3, 808.0], [77.4, 809.0], [77.5, 809.0], [77.6, 809.0], [77.7, 809.0], [77.8, 810.0], [77.9, 810.0], [78.0, 811.0], [78.1, 811.0], [78.2, 812.0], [78.3, 812.0], [78.4, 812.0], [78.5, 813.0], [78.6, 813.0], [78.7, 813.0], [78.8, 814.0], [78.9, 814.0], [79.0, 814.0], [79.1, 814.0], [79.2, 814.0], [79.3, 814.0], [79.4, 814.0], [79.5, 814.0], [79.6, 815.0], [79.7, 815.0], [79.8, 815.0], [79.9, 815.0], [80.0, 815.0], [80.1, 815.0], [80.2, 816.0], [80.3, 816.0], [80.4, 816.0], [80.5, 816.0], [80.6, 816.0], [80.7, 817.0], [80.8, 817.0], [80.9, 817.0], [81.0, 817.0], [81.1, 817.0], [81.2, 817.0], [81.3, 817.0], [81.4, 817.0], [81.5, 818.0], [81.6, 818.0], [81.7, 818.0], [81.8, 818.0], [81.9, 818.0], [82.0, 818.0], [82.1, 818.0], [82.2, 819.0], [82.3, 819.0], [82.4, 819.0], [82.5, 819.0], [82.6, 820.0], [82.7, 820.0], [82.8, 820.0], [82.9, 820.0], [83.0, 820.0], [83.1, 821.0], [83.2, 821.0], [83.3, 821.0], [83.4, 821.0], [83.5, 821.0], [83.6, 821.0], [83.7, 821.0], [83.8, 822.0], [83.9, 822.0], [84.0, 822.0], [84.1, 822.0], [84.2, 822.0], [84.3, 822.0], [84.4, 823.0], [84.5, 823.0], [84.6, 824.0], [84.7, 824.0], [84.8, 824.0], [84.9, 824.0], [85.0, 824.0], [85.1, 825.0], [85.2, 825.0], [85.3, 826.0], [85.4, 826.0], [85.5, 827.0], [85.6, 827.0], [85.7, 827.0], [85.8, 827.0], [85.9, 828.0], [86.0, 828.0], [86.1, 829.0], [86.2, 830.0], [86.3, 830.0], [86.4, 830.0], [86.5, 832.0], [86.6, 832.0], [86.7, 832.0], [86.8, 832.0], [86.9, 832.0], [87.0, 832.0], [87.1, 833.0], [87.2, 833.0], [87.3, 833.0], [87.4, 833.0], [87.5, 833.0], [87.6, 833.0], [87.7, 834.0], [87.8, 834.0], [87.9, 834.0], [88.0, 834.0], [88.1, 834.0], [88.2, 834.0], [88.3, 834.0], [88.4, 834.0], [88.5, 834.0], [88.6, 834.0], [88.7, 835.0], [88.8, 835.0], [88.9, 835.0], [89.0, 835.0], [89.1, 835.0], [89.2, 835.0], [89.3, 835.0], [89.4, 835.0], [89.5, 835.0], [89.6, 835.0], [89.7, 836.0], [89.8, 836.0], [89.9, 836.0], [90.0, 837.0], [90.1, 837.0], [90.2, 837.0], [90.3, 838.0], [90.4, 838.0], [90.5, 839.0], [90.6, 839.0], [90.7, 839.0], [90.8, 839.0], [90.9, 839.0], [91.0, 839.0], [91.1, 840.0], [91.2, 840.0], [91.3, 840.0], [91.4, 840.0], [91.5, 841.0], [91.6, 841.0], [91.7, 841.0], [91.8, 841.0], [91.9, 841.0], [92.0, 842.0], [92.1, 842.0], [92.2, 842.0], [92.3, 843.0], [92.4, 843.0], [92.5, 843.0], [92.6, 844.0], [92.7, 844.0], [92.8, 845.0], [92.9, 845.0], [93.0, 845.0], [93.1, 846.0], [93.2, 846.0], [93.3, 846.0], [93.4, 846.0], [93.5, 846.0], [93.6, 846.0], [93.7, 846.0], [93.8, 846.0], [93.9, 847.0], [94.0, 847.0], [94.1, 847.0], [94.2, 847.0], [94.3, 847.0], [94.4, 848.0], [94.5, 848.0], [94.6, 848.0], [94.7, 849.0], [94.8, 849.0], [94.9, 849.0], [95.0, 850.0], [95.1, 850.0], [95.2, 850.0], [95.3, 850.0], [95.4, 850.0], [95.5, 851.0], [95.6, 851.0], [95.7, 851.0], [95.8, 851.0], [95.9, 851.0], [96.0, 851.0], [96.1, 851.0], [96.2, 852.0], [96.3, 852.0], [96.4, 852.0], [96.5, 853.0], [96.6, 853.0], [96.7, 853.0], [96.8, 853.0], [96.9, 853.0], [97.0, 853.0], [97.1, 853.0], [97.2, 854.0], [97.3, 854.0], [97.4, 854.0], [97.5, 854.0], [97.6, 854.0], [97.7, 855.0], [97.8, 855.0], [97.9, 855.0], [98.0, 856.0], [98.1, 856.0], [98.2, 856.0], [98.3, 856.0], [98.4, 856.0], [98.5, 857.0], [98.6, 857.0], [98.7, 857.0], [98.8, 858.0], [98.9, 858.0], [99.0, 858.0], [99.1, 858.0], [99.2, 858.0], [99.3, 859.0], [99.4, 860.0], [99.5, 860.0], [99.6, 860.0], [99.7, 862.0], [99.8, 862.0], [99.9, 867.0], [100.0, 867.0]], "isOverall": false, "label": "POST create order v4", "isController": false}], "supportsControllersDiscrimination": true, "maxX": 100.0, "title": "Response Time Percentiles"}},
        getOptions: function() {
            return {
                series: {
                    points: { show: false }
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimePercentiles'
                },
                xaxis: {
                    tickDecimals: 1,
                    axisLabel: "Percentiles",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Percentile value in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : %x.2 percentile was %y ms"
                },
                selection: { mode: "xy" },
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimePercentiles"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimesPercentiles"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimesPercentiles"), dataset, prepareOverviewOptions(options));
        }
};

/**
 * @param elementId Id of element where we display message
 */
function setEmptyGraph(elementId) {
    $(function() {
        $(elementId).text("No graph series with filter="+seriesFilter);
    });
}

// Response times percentiles
function refreshResponseTimePercentiles() {
    var infos = responseTimePercentilesInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimePercentiles");
        return;
    }
    if (isGraph($("#flotResponseTimesPercentiles"))){
        infos.createGraph();
    } else {
        var choiceContainer = $("#choicesResponseTimePercentiles");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimesPercentiles", "#overviewResponseTimesPercentiles");
        $('#bodyResponseTimePercentiles .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var responseTimeDistributionInfos = {
        data: {"result": {"minY": 46.0, "minX": 0.0, "maxY": 241.0, "series": [{"data": [[0.0, 46.0], [300.0, 101.0], [600.0, 115.0], [700.0, 131.0], [100.0, 76.0], [200.0, 68.0], [400.0, 87.0], [800.0, 241.0], [500.0, 134.0]], "isOverall": false, "label": "POST create order v4", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 100, "maxX": 800.0, "title": "Response Time Distribution"}},
        getOptions: function() {
            var granularity = this.data.result.granularity;
            return {
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimeDistribution'
                },
                xaxis:{
                    axisLabel: "Response times in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of responses",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                bars : {
                    show: true,
                    barWidth: this.data.result.granularity
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: function(label, xval, yval, flotItem){
                        return yval + " responses for " + label + " were between " + xval + " and " + (xval + granularity) + " ms";
                    }
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimeDistribution"), prepareData(data.result.series, $("#choicesResponseTimeDistribution")), options);
        }

};

// Response time distribution
function refreshResponseTimeDistribution() {
    var infos = responseTimeDistributionInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimeDistribution");
        return;
    }
    if (isGraph($("#flotResponseTimeDistribution"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimeDistribution");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        $('#footerResponseTimeDistribution .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var syntheticResponseTimeDistributionInfos = {
        data: {"result": {"minY": 380.0, "minX": 0.0, "ticks": [[0, "Requests having \nresponse time <= 500ms"], [1, "Requests having \nresponse time > 500ms and <= 1,500ms"], [2, "Requests having \nresponse time > 1,500ms"], [3, "Requests in error"]], "maxY": 619.0, "series": [{"data": [[0.0, 380.0]], "color": "#9ACD32", "isOverall": false, "label": "Requests having \nresponse time <= 500ms", "isController": false}, {"data": [[1.0, 619.0]], "color": "yellow", "isOverall": false, "label": "Requests having \nresponse time > 500ms and <= 1,500ms", "isController": false}, {"data": [], "color": "orange", "isOverall": false, "label": "Requests having \nresponse time > 1,500ms", "isController": false}, {"data": [], "color": "#FF6347", "isOverall": false, "label": "Requests in error", "isController": false}], "supportsControllersDiscrimination": false, "maxX": 1.0, "title": "Synthetic Response Times Distribution"}},
        getOptions: function() {
            return {
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendSyntheticResponseTimeDistribution'
                },
                xaxis:{
                    axisLabel: "Response times ranges",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                    tickLength:0,
                    min:-0.5,
                    max:3.5
                },
                yaxis: {
                    axisLabel: "Number of responses",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                bars : {
                    show: true,
                    align: "center",
                    barWidth: 0.25,
                    fill:.75
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: function(label, xval, yval, flotItem){
                        return yval + " " + label;
                    }
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var options = this.getOptions();
            prepareOptions(options, data);
            options.xaxis.ticks = data.result.ticks;
            $.plot($("#flotSyntheticResponseTimeDistribution"), prepareData(data.result.series, $("#choicesSyntheticResponseTimeDistribution")), options);
        }

};

// Response time distribution
function refreshSyntheticResponseTimeDistribution() {
    var infos = syntheticResponseTimeDistributionInfos;
    prepareSeries(infos.data, true);
    if (isGraph($("#flotSyntheticResponseTimeDistribution"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesSyntheticResponseTimeDistribution");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        $('#footerSyntheticResponseTimeDistribution .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var activeThreadsOverTimeInfos = {
        data: {"result": {"minY": 54.86686686686686, "minX": 1.77912924E12, "maxY": 54.86686686686686, "series": [{"data": [[1.77912924E12, 54.86686686686686]], "isOverall": false, "label": "Create Order V4 Threads", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77912924E12, "title": "Active Threads Over Time"}},
        getOptions: function() {
            return {
                series: {
                    stack: true,
                    lines: {
                        show: true,
                        fill: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of active threads",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 6,
                    show: true,
                    container: '#legendActiveThreadsOverTime'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                selection: {
                    mode: 'xy'
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : At %x there were %y active threads"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesActiveThreadsOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotActiveThreadsOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewActiveThreadsOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Active Threads Over Time
function refreshActiveThreadsOverTime(fixTimestamps) {
    var infos = activeThreadsOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 28800000);
    }
    if(isGraph($("#flotActiveThreadsOverTime"))) {
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesActiveThreadsOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotActiveThreadsOverTime", "#overviewActiveThreadsOverTime");
        $('#footerActiveThreadsOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var timeVsThreadsInfos = {
        data: {"result": {"minY": 148.875, "minX": 1.0, "maxY": 856.2307692307693, "series": [{"data": [[2.0, 208.8], [3.0, 174.5], [4.0, 148.875], [5.0, 155.125], [6.0, 164.625], [7.0, 173.25], [8.0, 176.22222222222223], [9.0, 191.375], [10.0, 209.125], [11.0, 207.77777777777777], [12.0, 213.77777777777777], [13.0, 233.75], [14.0, 235.11111111111111], [15.0, 242.88888888888889], [16.0, 247.88888888888889], [17.0, 256.0], [18.0, 265.77777777777777], [19.0, 276.22222222222223], [20.0, 290.22222222222223], [21.0, 300.22222222222223], [22.0, 329.1428571428571], [23.0, 332.6666666666667], [24.0, 342.2222222222223], [25.0, 351.0], [26.0, 346.1111111111111], [27.0, 350.20000000000005], [28.0, 371.0], [29.0, 368.7], [30.0, 371.0], [31.0, 385.55555555555554], [32.0, 381.9], [33.0, 401.77777777777777], [34.0, 409.77777777777777], [35.0, 422.1111111111111], [36.0, 422.1818181818182], [37.0, 435.33333333333337], [38.0, 437.5], [39.0, 442.1], [40.0, 449.5], [41.0, 454.4], [42.0, 461.0], [43.0, 473.3333333333333], [44.0, 488.29999999999995], [45.0, 506.44444444444446], [46.0, 509.09999999999997], [47.0, 522.4], [48.0, 530.4], [49.0, 529.4], [50.0, 540.3333333333334], [51.0, 551.3], [52.0, 550.5454545454545], [53.0, 559.5], [54.0, 571.0], [55.0, 572.7], [56.0, 572.5833333333333], [57.0, 584.1111111111112], [58.0, 591.1818181818182], [59.0, 597.3], [60.0, 600.2727272727273], [61.0, 604.9], [62.0, 608.4166666666666], [63.0, 621.3333333333335], [64.0, 642.75], [65.0, 658.7272727272727], [66.0, 666.7272727272727], [67.0, 677.2], [68.0, 682.909090909091], [69.0, 691.7272727272729], [70.0, 702.4999999999999], [71.0, 693.0000000000001], [72.0, 687.5454545454545], [73.0, 696.2727272727273], [74.0, 704.5454545454545], [75.0, 708.0909090909091], [76.0, 711.0833333333334], [77.0, 721.2], [78.0, 732.5], [79.0, 735.8333333333334], [80.0, 734.7499999999999], [81.0, 734.3333333333334], [82.0, 743.6], [83.0, 752.2499999999999], [84.0, 755.6363636363636], [85.0, 766.5555555555555], [86.0, 780.6363636363636], [87.0, 789.8333333333334], [88.0, 795.9090909090909], [89.0, 807.4545454545455], [90.0, 814.25], [91.0, 818.75], [92.0, 833.1], [93.0, 845.0999999999999], [94.0, 839.0000000000001], [95.0, 837.0909090909091], [96.0, 840.8461538461538], [97.0, 782.3333333333334], [98.0, 849.5833333333334], [99.0, 856.2307692307693], [100.0, 852.3199999999999], [1.0, 804.0]], "isOverall": false, "label": "POST create order v4", "isController": false}, {"data": [[54.86686686686686, 556.3253253253248]], "isOverall": false, "label": "POST create order v4-Aggregated", "isController": false}], "supportsControllersDiscrimination": true, "maxX": 100.0, "title": "Time VS Threads"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    axisLabel: "Number of active threads",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response times in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: { noColumns: 2,show: true, container: '#legendTimeVsThreads' },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s: At %x.2 active threads, Average response time was %y.2 ms"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesTimeVsThreads"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotTimesVsThreads"), dataset, options);
            // setup overview
            $.plot($("#overviewTimesVsThreads"), dataset, prepareOverviewOptions(options));
        }
};

// Time vs threads
function refreshTimeVsThreads(){
    var infos = timeVsThreadsInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyTimeVsThreads");
        return;
    }
    if(isGraph($("#flotTimesVsThreads"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTimeVsThreads");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTimesVsThreads", "#overviewTimesVsThreads");
        $('#footerTimeVsThreads .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var bytesThroughputOverTimeInfos = {
        data : {"result": {"minY": 2930.4, "minX": 1.77912924E12, "maxY": 5827.5, "series": [{"data": [[1.77912924E12, 2930.4]], "isOverall": false, "label": "Bytes received per second", "isController": false}, {"data": [[1.77912924E12, 5827.5]], "isOverall": false, "label": "Bytes sent per second", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77912924E12, "title": "Bytes Throughput Over Time"}},
        getOptions : function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity) ,
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Bytes / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendBytesThroughputOverTime'
                },
                selection: {
                    mode: "xy"
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y"
                }
            };
        },
        createGraph : function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesBytesThroughputOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotBytesThroughputOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewBytesThroughputOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Bytes throughput Over Time
function refreshBytesThroughputOverTime(fixTimestamps) {
    var infos = bytesThroughputOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 28800000);
    }
    if(isGraph($("#flotBytesThroughputOverTime"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesBytesThroughputOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotBytesThroughputOverTime", "#overviewBytesThroughputOverTime");
        $('#footerBytesThroughputOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var responseTimesOverTimeInfos = {
        data: {"result": {"minY": 556.3253253253248, "minX": 1.77912924E12, "maxY": 556.3253253253248, "series": [{"data": [[1.77912924E12, 556.3253253253248]], "isOverall": false, "label": "POST create order v4", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77912924E12, "title": "Response Time Over Time"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average response time was %y ms"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Response Times Over Time
function refreshResponseTimeOverTime(fixTimestamps) {
    var infos = responseTimesOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimeOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 28800000);
    }
    if(isGraph($("#flotResponseTimesOverTime"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimesOverTime", "#overviewResponseTimesOverTime");
        $('#footerResponseTimesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var latenciesOverTimeInfos = {
        data: {"result": {"minY": 556.2942942942938, "minX": 1.77912924E12, "maxY": 556.2942942942938, "series": [{"data": [[1.77912924E12, 556.2942942942938]], "isOverall": false, "label": "POST create order v4", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77912924E12, "title": "Latencies Over Time"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response latencies in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendLatenciesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average latency was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesLatenciesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotLatenciesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewLatenciesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Latencies Over Time
function refreshLatenciesOverTime(fixTimestamps) {
    var infos = latenciesOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyLatenciesOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 28800000);
    }
    if(isGraph($("#flotLatenciesOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesLatenciesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotLatenciesOverTime", "#overviewLatenciesOverTime");
        $('#footerLatenciesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var connectTimeOverTimeInfos = {
        data: {"result": {"minY": 0.07807807807807798, "minX": 1.77912924E12, "maxY": 0.07807807807807798, "series": [{"data": [[1.77912924E12, 0.07807807807807798]], "isOverall": false, "label": "POST create order v4", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77912924E12, "title": "Connect Time Over Time"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getConnectTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average Connect Time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendConnectTimeOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average connect time was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesConnectTimeOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotConnectTimeOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewConnectTimeOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Connect Time Over Time
function refreshConnectTimeOverTime(fixTimestamps) {
    var infos = connectTimeOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyConnectTimeOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 28800000);
    }
    if(isGraph($("#flotConnectTimeOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesConnectTimeOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotConnectTimeOverTime", "#overviewConnectTimeOverTime");
        $('#footerConnectTimeOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var responseTimePercentilesOverTimeInfos = {
        data: {"result": {"minY": 26.0, "minX": 1.77912924E12, "maxY": 867.0, "series": [{"data": [[1.77912924E12, 867.0]], "isOverall": false, "label": "Max", "isController": false}, {"data": [[1.77912924E12, 26.0]], "isOverall": false, "label": "Min", "isController": false}, {"data": [[1.77912924E12, 837.0]], "isOverall": false, "label": "90th percentile", "isController": false}, {"data": [[1.77912924E12, 858.0]], "isOverall": false, "label": "99th percentile", "isController": false}, {"data": [[1.77912924E12, 589.0]], "isOverall": false, "label": "Median", "isController": false}, {"data": [[1.77912924E12, 850.0]], "isOverall": false, "label": "95th percentile", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77912924E12, "title": "Response Time Percentiles Over Time (successful requests only)"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true,
                        fill: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Response Time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimePercentilesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Response time was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimePercentilesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimePercentilesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimePercentilesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Response Time Percentiles Over Time
function refreshResponseTimePercentilesOverTime(fixTimestamps) {
    var infos = responseTimePercentilesOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 28800000);
    }
    if(isGraph($("#flotResponseTimePercentilesOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesResponseTimePercentilesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimePercentilesOverTime", "#overviewResponseTimePercentilesOverTime");
        $('#footerResponseTimePercentilesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var responseTimeVsRequestInfos = {
    data: {"result": {"minY": 47.5, "minX": 6.0, "maxY": 847.0, "series": [{"data": [[73.0, 98.0], [78.0, 196.0], [82.0, 576.5], [88.0, 433.5], [94.0, 568.0], [93.0, 667.0], [6.0, 47.5], [102.0, 720.5], [100.0, 793.0], [113.0, 847.0]], "isOverall": false, "label": "Successes", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 1000, "maxX": 113.0, "title": "Response Time Vs Request"}},
    getOptions: function() {
        return {
            series: {
                lines: {
                    show: false
                },
                points: {
                    show: true
                }
            },
            xaxis: {
                axisLabel: "Global number of requests per second",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            yaxis: {
                axisLabel: "Median Response Time in ms",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            legend: {
                noColumns: 2,
                show: true,
                container: '#legendResponseTimeVsRequest'
            },
            selection: {
                mode: 'xy'
            },
            grid: {
                hoverable: true // IMPORTANT! this is needed for tooltip to work
            },
            tooltip: true,
            tooltipOpts: {
                content: "%s : Median response time at %x req/s was %y ms"
            },
            colors: ["#9ACD32", "#FF6347"]
        };
    },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesResponseTimeVsRequest"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotResponseTimeVsRequest"), dataset, options);
        // setup overview
        $.plot($("#overviewResponseTimeVsRequest"), dataset, prepareOverviewOptions(options));

    }
};

// Response Time vs Request
function refreshResponseTimeVsRequest() {
    var infos = responseTimeVsRequestInfos;
    prepareSeries(infos.data);
    if (isGraph($("#flotResponseTimeVsRequest"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimeVsRequest");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimeVsRequest", "#overviewResponseTimeVsRequest");
        $('#footerResponseRimeVsRequest .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var latenciesVsRequestInfos = {
    data: {"result": {"minY": 47.5, "minX": 6.0, "maxY": 847.0, "series": [{"data": [[73.0, 98.0], [78.0, 196.0], [82.0, 576.5], [88.0, 433.0], [94.0, 568.0], [93.0, 667.0], [6.0, 47.5], [102.0, 720.5], [100.0, 793.0], [113.0, 847.0]], "isOverall": false, "label": "Successes", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 1000, "maxX": 113.0, "title": "Latencies Vs Request"}},
    getOptions: function() {
        return{
            series: {
                lines: {
                    show: false
                },
                points: {
                    show: true
                }
            },
            xaxis: {
                axisLabel: "Global number of requests per second",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            yaxis: {
                axisLabel: "Median Latency in ms",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            legend: { noColumns: 2,show: true, container: '#legendLatencyVsRequest' },
            selection: {
                mode: 'xy'
            },
            grid: {
                hoverable: true // IMPORTANT! this is needed for tooltip to work
            },
            tooltip: true,
            tooltipOpts: {
                content: "%s : Median Latency time at %x req/s was %y ms"
            },
            colors: ["#9ACD32", "#FF6347"]
        };
    },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesLatencyVsRequest"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotLatenciesVsRequest"), dataset, options);
        // setup overview
        $.plot($("#overviewLatenciesVsRequest"), dataset, prepareOverviewOptions(options));
    }
};

// Latencies vs Request
function refreshLatenciesVsRequest() {
        var infos = latenciesVsRequestInfos;
        prepareSeries(infos.data);
        if(isGraph($("#flotLatenciesVsRequest"))){
            infos.createGraph();
        }else{
            var choiceContainer = $("#choicesLatencyVsRequest");
            createLegend(choiceContainer, infos);
            infos.createGraph();
            setGraphZoomable("#flotLatenciesVsRequest", "#overviewLatenciesVsRequest");
            $('#footerLatenciesVsRequest .legendColorBox > div').each(function(i){
                $(this).clone().prependTo(choiceContainer.find("li").eq(i));
            });
        }
};

var hitsPerSecondInfos = {
        data: {"result": {"minY": 16.65, "minX": 1.77912924E12, "maxY": 16.65, "series": [{"data": [[1.77912924E12, 16.65]], "isOverall": false, "label": "hitsPerSecond", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77912924E12, "title": "Hits Per Second"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of hits / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendHitsPerSecond"
                },
                selection: {
                    mode : 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y.2 hits/sec"
                }
            };
        },
        createGraph: function createGraph() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesHitsPerSecond"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotHitsPerSecond"), dataset, options);
            // setup overview
            $.plot($("#overviewHitsPerSecond"), dataset, prepareOverviewOptions(options));
        }
};

// Hits per second
function refreshHitsPerSecond(fixTimestamps) {
    var infos = hitsPerSecondInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 28800000);
    }
    if (isGraph($("#flotHitsPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesHitsPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotHitsPerSecond", "#overviewHitsPerSecond");
        $('#footerHitsPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var codesPerSecondInfos = {
        data: {"result": {"minY": 16.65, "minX": 1.77912924E12, "maxY": 16.65, "series": [{"data": [[1.77912924E12, 16.65]], "isOverall": false, "label": "200", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77912924E12, "title": "Codes Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of responses / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendCodesPerSecond"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "Number of Response Codes %s at %x was %y.2 responses / sec"
                }
            };
        },
    createGraph: function() {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesCodesPerSecond"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotCodesPerSecond"), dataset, options);
        // setup overview
        $.plot($("#overviewCodesPerSecond"), dataset, prepareOverviewOptions(options));
    }
};

// Codes per second
function refreshCodesPerSecond(fixTimestamps) {
    var infos = codesPerSecondInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 28800000);
    }
    if(isGraph($("#flotCodesPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesCodesPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotCodesPerSecond", "#overviewCodesPerSecond");
        $('#footerCodesPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var transactionsPerSecondInfos = {
        data: {"result": {"minY": 16.65, "minX": 1.77912924E12, "maxY": 16.65, "series": [{"data": [[1.77912924E12, 16.65]], "isOverall": false, "label": "POST create order v4-success", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77912924E12, "title": "Transactions Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of transactions / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendTransactionsPerSecond"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y transactions / sec"
                }
            };
        },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesTransactionsPerSecond"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotTransactionsPerSecond"), dataset, options);
        // setup overview
        $.plot($("#overviewTransactionsPerSecond"), dataset, prepareOverviewOptions(options));
    }
};

// Transactions per second
function refreshTransactionsPerSecond(fixTimestamps) {
    var infos = transactionsPerSecondInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyTransactionsPerSecond");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 28800000);
    }
    if(isGraph($("#flotTransactionsPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTransactionsPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTransactionsPerSecond", "#overviewTransactionsPerSecond");
        $('#footerTransactionsPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var totalTPSInfos = {
        data: {"result": {"minY": 16.65, "minX": 1.77912924E12, "maxY": 16.65, "series": [{"data": [[1.77912924E12, 16.65]], "isOverall": false, "label": "Transaction-success", "isController": false}, {"data": [], "isOverall": false, "label": "Transaction-failure", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77912924E12, "title": "Total Transactions Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of transactions / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendTotalTPS"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y transactions / sec"
                },
                colors: ["#9ACD32", "#FF6347"]
            };
        },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesTotalTPS"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotTotalTPS"), dataset, options);
        // setup overview
        $.plot($("#overviewTotalTPS"), dataset, prepareOverviewOptions(options));
    }
};

// Total Transactions per second
function refreshTotalTPS(fixTimestamps) {
    var infos = totalTPSInfos;
    // We want to ignore seriesFilter
    prepareSeries(infos.data, false, true);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 28800000);
    }
    if(isGraph($("#flotTotalTPS"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTotalTPS");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTotalTPS", "#overviewTotalTPS");
        $('#footerTotalTPS .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

// Collapse the graph matching the specified DOM element depending the collapsed
// status
function collapse(elem, collapsed){
    if(collapsed){
        $(elem).parent().find(".fa-chevron-up").removeClass("fa-chevron-up").addClass("fa-chevron-down");
    } else {
        $(elem).parent().find(".fa-chevron-down").removeClass("fa-chevron-down").addClass("fa-chevron-up");
        if (elem.id == "bodyBytesThroughputOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshBytesThroughputOverTime(true);
            }
            document.location.href="#bytesThroughputOverTime";
        } else if (elem.id == "bodyLatenciesOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshLatenciesOverTime(true);
            }
            document.location.href="#latenciesOverTime";
        } else if (elem.id == "bodyCustomGraph") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshCustomGraph(true);
            }
            document.location.href="#responseCustomGraph";
        } else if (elem.id == "bodyConnectTimeOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshConnectTimeOverTime(true);
            }
            document.location.href="#connectTimeOverTime";
        } else if (elem.id == "bodyResponseTimePercentilesOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimePercentilesOverTime(true);
            }
            document.location.href="#responseTimePercentilesOverTime";
        } else if (elem.id == "bodyResponseTimeDistribution") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimeDistribution();
            }
            document.location.href="#responseTimeDistribution" ;
        } else if (elem.id == "bodySyntheticResponseTimeDistribution") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshSyntheticResponseTimeDistribution();
            }
            document.location.href="#syntheticResponseTimeDistribution" ;
        } else if (elem.id == "bodyActiveThreadsOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshActiveThreadsOverTime(true);
            }
            document.location.href="#activeThreadsOverTime";
        } else if (elem.id == "bodyTimeVsThreads") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTimeVsThreads();
            }
            document.location.href="#timeVsThreads" ;
        } else if (elem.id == "bodyCodesPerSecond") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshCodesPerSecond(true);
            }
            document.location.href="#codesPerSecond";
        } else if (elem.id == "bodyTransactionsPerSecond") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTransactionsPerSecond(true);
            }
            document.location.href="#transactionsPerSecond";
        } else if (elem.id == "bodyTotalTPS") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTotalTPS(true);
            }
            document.location.href="#totalTPS";
        } else if (elem.id == "bodyResponseTimeVsRequest") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimeVsRequest();
            }
            document.location.href="#responseTimeVsRequest";
        } else if (elem.id == "bodyLatenciesVsRequest") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshLatenciesVsRequest();
            }
            document.location.href="#latencyVsRequest";
        }
    }
}

/*
 * Activates or deactivates all series of the specified graph (represented by id parameter)
 * depending on checked argument.
 */
function toggleAll(id, checked){
    var placeholder = document.getElementById(id);

    var cases = $(placeholder).find(':checkbox');
    cases.prop('checked', checked);
    $(cases).parent().children().children().toggleClass("legend-disabled", !checked);

    var choiceContainer;
    if ( id == "choicesBytesThroughputOverTime"){
        choiceContainer = $("#choicesBytesThroughputOverTime");
        refreshBytesThroughputOverTime(false);
    } else if(id == "choicesResponseTimesOverTime"){
        choiceContainer = $("#choicesResponseTimesOverTime");
        refreshResponseTimeOverTime(false);
    }else if(id == "choicesResponseCustomGraph"){
        choiceContainer = $("#choicesResponseCustomGraph");
        refreshCustomGraph(false);
    } else if ( id == "choicesLatenciesOverTime"){
        choiceContainer = $("#choicesLatenciesOverTime");
        refreshLatenciesOverTime(false);
    } else if ( id == "choicesConnectTimeOverTime"){
        choiceContainer = $("#choicesConnectTimeOverTime");
        refreshConnectTimeOverTime(false);
    } else if ( id == "choicesResponseTimePercentilesOverTime"){
        choiceContainer = $("#choicesResponseTimePercentilesOverTime");
        refreshResponseTimePercentilesOverTime(false);
    } else if ( id == "choicesResponseTimePercentiles"){
        choiceContainer = $("#choicesResponseTimePercentiles");
        refreshResponseTimePercentiles();
    } else if(id == "choicesActiveThreadsOverTime"){
        choiceContainer = $("#choicesActiveThreadsOverTime");
        refreshActiveThreadsOverTime(false);
    } else if ( id == "choicesTimeVsThreads"){
        choiceContainer = $("#choicesTimeVsThreads");
        refreshTimeVsThreads();
    } else if ( id == "choicesSyntheticResponseTimeDistribution"){
        choiceContainer = $("#choicesSyntheticResponseTimeDistribution");
        refreshSyntheticResponseTimeDistribution();
    } else if ( id == "choicesResponseTimeDistribution"){
        choiceContainer = $("#choicesResponseTimeDistribution");
        refreshResponseTimeDistribution();
    } else if ( id == "choicesHitsPerSecond"){
        choiceContainer = $("#choicesHitsPerSecond");
        refreshHitsPerSecond(false);
    } else if(id == "choicesCodesPerSecond"){
        choiceContainer = $("#choicesCodesPerSecond");
        refreshCodesPerSecond(false);
    } else if ( id == "choicesTransactionsPerSecond"){
        choiceContainer = $("#choicesTransactionsPerSecond");
        refreshTransactionsPerSecond(false);
    } else if ( id == "choicesTotalTPS"){
        choiceContainer = $("#choicesTotalTPS");
        refreshTotalTPS(false);
    } else if ( id == "choicesResponseTimeVsRequest"){
        choiceContainer = $("#choicesResponseTimeVsRequest");
        refreshResponseTimeVsRequest();
    } else if ( id == "choicesLatencyVsRequest"){
        choiceContainer = $("#choicesLatencyVsRequest");
        refreshLatenciesVsRequest();
    }
    var color = checked ? "black" : "#818181";
    if(choiceContainer != null) {
        choiceContainer.find("label").each(function(){
            this.style.color = color;
        });
    }
}


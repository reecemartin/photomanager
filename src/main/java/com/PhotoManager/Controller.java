void height_check(int height_requirement, int rider1_height, int rider2_height, int special_feature){

        int valid = 0;
        int special = (rider1_height + rider2_height - (special_feature * 5)) * special_feature;

        if(height_requirement < 48){
        if(rider1_height <= height_requirement || rider2_height <= height_requirement){
        valid = 1;
        }
        }

        if(height_requirement == 48){
        if(rider1_height >= height_requirement && rider1_height < 56 && rider2_height >= height_requirement && rider2_height < 56){
        valid = 1;
        }
        }

        if(height_requirement > 48){
        if(rider1_height >= height_requirement && rider2_height >= height_requirement){
        valid = 1;
        }
        }



        if(valid == 1){
        printf("Enter");
        if(height_requirement % special == 0){
        printf("Bonus: Advance to the front of the line!");
        }
        }else{
        printf("Sorry, height check did not pass");
        }



        }
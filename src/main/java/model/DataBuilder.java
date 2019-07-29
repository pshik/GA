package model;

public class DataBuilder {
    private String[] values;

    public DataBuilder() {
        values = new String[6];
        for (int i = 0; i < values.length; i++)
            values[i] = " ";
    }

    public String getValue(int position) {
        return values[position];
    }

    public void setValue(String value, int num){
        values[num] = value;
    }
    public void setValue(int position,String value) {
        switch (position){
            case 0:
                values[1] = "#";
                values[2] = "#";
                values[3] = "#";
                values[4] = "#";
                values[5] = "#";
                break;
            case 1:
                values[0] = "#";
                values[3] = "#";
                values[4] = "#";
                break;
            case 2:
                values[0] = "#";
                values[4] = "#";
                values[5] = "#";
                break;
            case 3:
                values[0] = "#";
                values[1] = "#";
                break;
            case 4:
                values[0] = "#";
                values[1] = "#";
                values[2] = "#";
                break;
            case 5:
                values[0] = "#";
                values[2] = "#";
                break;
        }
        this.values[position] = value;
    }
    public void highlightValue(int position,String type){
        switch (type){
            case "pickUp":
                this.values[position] = "@" + values[position];
                break;
            case "showAll":
                this.values[position] = "^" + values[position];
                break;
            case "available":
                this.values[position] = "*" + values[position];
                break;
        }

    }
    public void fillValues(String s){
        for (int i = 0; i < values.length; i++)
            values[i] = s.split(",")[i];
    }
    public void clear(){
        for (int i = 0; i < values.length; i++)
            values[i] = " ";
    }
    public void removeHighlighting(int position){
        this.values[position] = values[position].substring(1);
    }
    public void removeHighlighting(){
        for (int i = 0; i < values.length; i++){
            this.values[i].replaceAll("(@)(^)(\\*)","");
        }
    }
    @Override
    public String toString() {
        return values[0] + "," + values[1] + "," + values[2] + "," + values[3] + "," + values[4] + "," + values[5];
    }
    public int size(){
        return values.length;
    }

    public void highlightFreeCell(int size) {
        switch (size){
            case 1:
                values[0] = "*";
                values[1] = "*";
                values[2] = "*";
                values[3] = "*";
                values[4] = "*";
                values[5] = "*";
                break;
            case 2:
                values[0] = "*";
                values[1] = "*";
                values[2] = "*";
                values[3] = " ";
                values[4] = " ";
                values[5] = " ";
                break;
            case 3:
                values[0] = "*";
                values[1] = " ";
                values[2] = " ";
                values[3] = " ";
                values[4] = " ";
                values[5] = " ";
                break;
        }
    }

    public void checkAvailablePositions(int size) {
        switch (size){
            case 1:
                if (values[0].equals(" "))
                    highlightValue(0,"available");
                if (values[1].equals(" "))
                    highlightValue(1,"available");
                if (values[2].equals(" "))
                    highlightValue(2,"available");
                if (values[3].equals(" "))
                    highlightValue(3,"available");
                if (values[4].equals(" "))
                    highlightValue(4,"available");
                if (values[5].equals(" "))
                    highlightValue(5,"available");
                break;
            case 2:
                if (values[0].equals(" "))
                    highlightValue(0,"available");
                if (values[1].equals(" "))
                    highlightValue(1,"available");
                if (values[2].equals(" "))
                    highlightValue(2,"available");
                break;
            case 3:
                if (values[0].equals(" "))
                    highlightValue(0,"available");
                break;
        }
    }
}

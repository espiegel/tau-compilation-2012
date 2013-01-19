package IC.LIR;
import java.util.HashMap;
import java.util.Map;

import IC.AST.Field;
import IC.AST.ICClass;
import IC.AST.Method;

public class ClassLayout {
	ICClass self;
	private Map<Field,Integer> fieldToOffsetMap = new HashMap<Field,Integer>(); //stores fields offset
	private Map<Method,Integer> methodToOffsetMap = new HashMap<Method,Integer>(); //stores virtual methods offset
	
	private Map<String,Method> nameToMethodMap = new HashMap<String,Method>(); //to handle method overriding
	
	public ClassLayout(ICClass A, ClassLayout B){ // "A extends B"
		
		self = A;
		
		if (B != null){
			methodToOffsetMap.putAll(B.methodToOffsetMap);
			fieldToOffsetMap.putAll(B.fieldToOffsetMap);
			nameToMethodMap.putAll(B.nameToMethodMap);
		}
		
		for (Method method : A.getMethods()){
			addMethod(method); //add all virtual methods
		}
		
		for (Field field : A.getFields()){
			addField(field); //add all fields
		}
	}
	
	public ClassLayout(ICClass A){
		this(A,null);
	}
	
	private void addField(Field field) {
		fieldToOffsetMap.put(field, getTableOffset());
	}
	
	public int sizeof(){
		return (getTableOffset())*4;
	}
	
	private void addMethod(Method method) {
		if (method.isStatic()){ 
			return; //static methods are handled separately!
		}
		if (isOverriding(method)){
			String key = method.getName();
			
			//retrieve overridden method
			Method oldMethod = nameToMethodMap.get(key); 
			
			//put new method in map with old method offset
			methodToOffsetMap.put(method, methodToOffsetMap.remove(oldMethod));
			
			nameToMethodMap.remove(key); //update nameToMethodMap
			nameToMethodMap.put(key, method);
		}
		else{
			methodToOffsetMap.put(method,getDispatchVectorOffset());
			nameToMethodMap.put(method.getName(), method);
		}
	}

	private boolean isOverriding(Method method){
		return nameToMethodMap.containsKey(method.getName());
	}

	public Map<Field,Integer> getFieldToOffsetMap(){
		return fieldToOffsetMap;
	}
	
	public Map<Method,Integer> getMethodToOffsetMap(){
		return methodToOffsetMap;
	}
	
	public int getTableOffset(){
		return fieldToOffsetMap.size()+1; //return the class layout offset.
											//0 is reserved to class's DispatchVectorPtr
	}
	
	public int getDispatchVectorOffset(){
		return methodToOffsetMap.size(); //return Dispatch vector offset
	}
	
	public String getDispatchVector(){
		String myName = self.getName();
		String DV = getDispatchVectorLabel()+": [";
		for (int i=0; i<getDispatchVectorOffset(); i++){
			for (Method method: methodToOffsetMap.keySet()){
				
				if (method.isStatic()) continue;
				
				if (i == methodToOffsetMap.get(method)) {
					DV+='_'+myName+'_'+method.getName()+',';
					break;
				}
			}
		}
		return removeComma(DV)+']';
	}
	
	public String getDispatchVectorLabel(){
		String myName = self.getName();
		return "_DV_"+myName;
	}
	
	public String tableToString(){
		String table = "# fields offsets:\n";
		table += "# "+getDispatchVectorLabel()+": "+0+'\n';
        for(int i = 1; i < getTableOffset(); i++){
            for (Field f: fieldToOffsetMap.keySet()){
                    if (fieldToOffsetMap.get(f) == i){
                    	table += "# "+f.getName()+": ";
                    	table += i+"\n";
                        break;
                    }
            }
        }
        return table;
	}
	
	public static String removeComma(String string){
		if (string.endsWith(",")){
			return string.substring(0,string.length()-1);
		}
		else{
			return string;
		}
	}
	
	public String toString(){
		return tableToString()+getDispatchVector();
	}
}
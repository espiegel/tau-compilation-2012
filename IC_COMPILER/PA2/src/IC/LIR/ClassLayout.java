package IC.LIR;
import java.util.HashMap;
import java.util.Map;

import IC.AST.Field;
import IC.AST.ICClass;
import IC.AST.Method;

public class ClassLayout {
	ICClass self;
	private Map<String,Integer> fieldToOffsetMap = new HashMap<String,Integer>(); //stores fields offset
	private Map<String,Integer> methodToOffsetMap = new HashMap<String,Integer>(); //stores virtual methods offset
	private Map<String,Method> nameToMethodMap = new HashMap<String,Method>();
	
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
	
	public String getClassName(){
		return self.getName();
	}
	
	public ClassLayout(ICClass A){
		this(A,null);
	}
	
	private void addField(Field field) {
		fieldToOffsetMap.put(field.getName(), getTableOffset());
	}
	
	public int sizeof(){
		return (getTableOffset())*4;
	}
	
	private void addMethod(Method method) {

		String key = method.getName();
		if (!isOverriding(method) && !method.isStatic()){ // for overriding methods, offset stays the same.
			
			methodToOffsetMap.put(method.getName(),getDispatchVectorOffset());
		}
		nameToMethodMap.put(key, method);
	}
	
	public Method getMethod(String name){
		return nameToMethodMap.get(name);
	}

	private boolean isOverriding(Method method){
		return methodToOffsetMap.containsKey(method.getName());
	}
	
	public int getTableOffset(){
		return fieldToOffsetMap.size()+1; //return the class layout offset.
											//0 is reserved to class's DispatchVectorPtr
	}
	
	public int getDispatchVectorOffset(){
		return methodToOffsetMap.size(); //return Dispatch vector offset
	}
	
	public String getDispatchVector(){
		String DV = getDispatchVectorLabel()+": [";
		for (int i=0; i<getDispatchVectorOffset(); i++){
			for (String methodname: methodToOffsetMap.keySet()){
				Method method = nameToMethodMap.get(methodname);
				String className = method.getEnclosingScope().getStringId();
				if (i == methodToOffsetMap.get(methodname)) {
					DV+='_'+className+'_'+methodname+',';
					break;
				}
			}
		}
		return removeComma(DV)+']';
	}
	
	public String getDispatchVectorLabel(){
		return "_DV_"+self.getName();
	}
	
	public String tableToString(){
		String table = "# fields offsets:\n";
		table += "# "+getDispatchVectorLabel()+": "+0+'\n';
        for(int i = 1; i < getTableOffset(); i++){
            for (String fieldname: fieldToOffsetMap.keySet()){
                    if (fieldToOffsetMap.get(fieldname) == i){
                    	table += "# "+fieldname+": ";
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

	public int getFieldOffset(String name) {
		return fieldToOffsetMap.get(name);
	}
	
	public int getMethodOffset(String name) {
		return methodToOffsetMap.get(name);
	}
}
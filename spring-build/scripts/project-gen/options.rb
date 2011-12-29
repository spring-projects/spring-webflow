Choice.options do 
  
  header ""
  header "Spring Build Project Generator v1.0"
  
  option :project_name, :required => true do
    short '-n'
    long '--name'
    desc 'The name of the new project.'
  end
  
  option :target do
    short '-t'
    long '--target'
    desc 'The target directory in which to create the new project.'
    default '.'
  end
  
  option :organization do
    short '-o'
    long '--org'
    desc 'The organization name to include in the ivy.xml file.'
    default do 
      project_name = Choice.choices[:project_name]
      project_name.split('.')[0..1].join(".")
    end
  end
  
  option :archetype do
    short '-a'
    long '--archetype'
    desc 'The archetype of the project you are creating.'
    default 'standard'
  end
  
  footer ""
  footer "--help This message."
  
end

# Nifty hack to support lazy defaulting of choices as they are retreived
# Allows for blocks to be used as default values as well as static values
c = class << Choice.choices; self end
c.module_eval do
  
  alias :do_get :[]
  
  def [](name)
    v = do_get(name)
    if v.is_a? Proc
      v.call
    else
      v
    end
  end 
end

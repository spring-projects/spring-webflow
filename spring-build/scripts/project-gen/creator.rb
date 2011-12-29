require "rubygems"
require "fileutils"
require "erb"

module Creator extend self
  
  @excludes = [".svn", ".", "..", ".DS_Store"]
  
  def create(args)
    project_name = args[:project_name]
    target_dir = File.expand_path(args[:target])
    organization = args[:organization]
    archetype = args[:archetype]

    # locate the template directory
    template_dir = determine_template_dir()
    archetype_dir = determine_archetype_dir(archetype)
    
    # build the project
    project_dir = File.join(target_dir, project_name)

    FileUtils.remove_dir(project_dir, true)
    FileUtils.mkdir_p(project_dir)
    make_project(template_dir, archetype_dir, project_dir, binding)
  end
  
  def make_project(template_dir, archetype_dir, project_dir, binding)
    # make the basic project structure
    make_dir(template_dir, project_dir, binding)
    make_dir(archetype_dir, project_dir, binding)
  end
  
  def make_dir(template_dir, target_dir, binding)
    Dir.foreach(template_dir) do |entry|
      if include_entry?(entry)
        entry_path = File.join(template_dir, entry)
        target_path = File.join(target_dir, entry)
        if File.file?(entry_path)
          if(entry == 'deletes.rb')
            ruby = IO.readlines(entry_path).join
            deletes = eval(ruby)
            deletes.each do |del|
              del_path = File.join(target_dir, del)
              FileUtils.remove_dir(del_path, true)
            end
          elsif
            create_file_from_template(entry_path, target_path, binding)
          end
        elsif File.directory?(entry_path)
          FileUtils.mkdir(target_path) unless File.exists?(target_path)
          make_dir(entry_path, target_path, binding)
        end
      end
    end
  end
  
  def include_entry?(entry)
    not @excludes.include?(entry)
  end
  
  def determine_template_dir() 
    File.join(get_script_location(), "templates/standard")
  end

  def determine_archetype_dir(archetype)
    if File.exists?(archetype) then
      f = archetype
    elsif
      f = File.join(get_script_location(), "templates/#{archetype}")
    end
    if !File.exists?(f) then
      puts "Archetype '#{archetype}' is unrecognised."
      exit(-1)
    end
    f
  end

  def get_script_location()
    File.dirname(File.expand_path(__FILE__))
  end
  
  def create_file_from_template(template_path, target_path, binding)
    template = IO.readlines(template_path)
    
    erb = ERB.new(target_path.gsub(/\$\{(.*)\}/, "<%= \\1 %>"))
    target_path = erb.result(binding)
    
    erb = ERB.new(template.join)

    File.open(target_path, "w") do |f|
      f.puts(erb.result(binding))
    end
  end

end
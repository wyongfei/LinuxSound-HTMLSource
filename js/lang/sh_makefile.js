if (! this.sh_languages) {
  this.sh_languages = {};
}
sh_languages['makefile'] = [
  [
    {
      'regex': /^[a-zA-Z0-9_-]+[\s]*=/g,
      'style': 'sh_type'
    },
    {
      'regex': /^\.[a-zA-Z0-9_-]+[\s]*:/g,
      'style': 'sh_preproc'
    },
    {
      'regex': /@(?:.+)@/g,
      'style': 'sh_preproc'
    },
    {
      'regex': /^(?:[A-Za-z0-9_.\s-])+:/g,
      'style': 'sh_symbol'
    },
    {
      'regex': /%[a-zA-Z0-9_.-]+:%[a-zA-Z0-9_.-]+/g,
      'style': 'sh_string'
    },
    {
      'regex': /(?:[A-Za-z0-9_-]*)\.(?:[A-Za-z0-9_-]+)/g,
      'style': 'sh_normal'
    },
    {
      'regex': /\b(?:import)\b/g,
      'style': 'sh_preproc'
    },
    {
      'regex': /\b[+-]?(?:(?:0x[A-Fa-f0-9]+)|(?:(?:[\d]*\.)?[\d]+(?:[eE][+-]?[\d]+)?))u?(?:(?:int(?:8|16|32|64))|L)?\b/g,
      'style': 'sh_number'
    },
    {
      'regex': /\\"/g,
      'style': 'sh_normal'
    },
    {
      'regex': /\\'/g,
      'style': 'sh_normal'
    },
    {
      'next': 1,
      'regex': /"/g,
      'style': 'sh_string'
    },
    {
      'next': 2,
      'regex': /'/g,
      'style': 'sh_string'
    },
    {
      'regex': /function[ \t]+(?:[A-Za-z]|_)[A-Za-z0-9_]*[ \t]*(?:\(\))?/g,
      'style': 'sh_function'
    },
    {
      'regex': /(?:[A-Za-z]|_)[A-Za-z0-9_]*[ \t]*\(\)/g,
      'style': 'sh_function'
    },
    {
      'regex': /(?:[A-Za-z]*[-\/]+[A-Za-z]+)+/g,
      'style': 'sh_normal'
    },
    {
      'regex': /\b(?:alias|bg|bind|break|builtin|caller|case|command|compgen|complete|continue|declare|dirs|disown|do|done|elif|else|enable|esac|eval|exec|exit|export|false|fc|fg|fi|for|getopts|hash|help|history|if|in|jobs|let|local|logout|popd|printf|pushd|read|readonly|return|select|set|shift|shopt|source|suspend|test|then|times|trap|true|type|typeset|umask|unalias|unset|until|wait|while)\b/g,
      'style': 'sh_keyword'
    },
    {
      'regex': /(?:[A-Za-z]|_)[A-Za-z0-9_]*(?==)/g,
      'style': 'sh_variable'
    },
    {
      'regex': /\$\{(?:[^ \t]+)\}/g,
      'style': 'sh_variable'
    },
    {
      'regex': /\$\((?:[^ \t]+)\)/g,
      'style': 'sh_variable'
    },
    {
      'regex': /\$(?:[A-Za-z]|_)[A-Za-z0-9_]*/g,
      'style': 'sh_variable'
    },
    {
      'regex': /\$(?:[^ \t]{1})/g,
      'style': 'sh_variable'
    },
    {
      'regex': /~|!|%|\^|\*|\(|\)|\+|=|\[|\]|\\|:|;|,|\.|\/|\?|&|<|>|\||(?:##){2}|%%/g,
      'style': 'sh_symbol'
    },
    {
      'next': 3,
      'regex': /#/g,
      'style': 'sh_comment'
    }
  ],
  [
    {
      'exit': true,
      'regex': /"/g,
      'style': 'sh_string'
    },
    {
      'regex': /\\./g,
      'style': 'sh_specialchar'
    }
  ],
  [
    {
      'exit': true,
      'regex': /'/g,
      'style': 'sh_string'
    },
    {
      'regex': /\\./g,
      'style': 'sh_specialchar'
    }
  ],
  [
    {
      'exit': true,
      'regex': /$/g
    }
  ]
];

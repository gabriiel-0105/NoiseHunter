import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
export default createVuetify({
 theme: {
  defaultTheme:'light',
  themes:{light:{colors:{primary:'#123153',secondary:'#0F8FA4',background:'#F5F7FB'}}}
 }
})

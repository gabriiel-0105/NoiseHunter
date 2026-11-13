import API from './Api'

export const nomesTipos = {
  VEICULO_BARULHENTO: 'Veículo barulhento',
  DISPARO_SUSPEITO: 'Possível disparo',
  OUTRO_RUIDO: 'Outro ruído',
}
export const nomesStatus = { ATIVO: 'Ativo', INATIVO: 'Inativo', MANUTENCAO: 'Em manutenção' }
export function formatarData(data) { return data ? new Date(data).toLocaleString('pt-BR') : '—' }

export async function listarUsuarios() { return (await API.get('/api/usuarios')).data }
export async function criarUsuario(obj) { return (await API.post('/api/usuarios', obj)).data }
export async function editarUsuario(id, obj) { return (await API.put(`/api/usuarios/${id}`, obj)).data }
export async function excluirUsuario(id) { return API.delete(`/api/usuarios/${id}`) }

export async function listarDispositivos() { return (await API.get('/api/dispositivos')).data }
export async function criarDispositivo(obj) { return (await API.post('/api/dispositivos', obj)).data }
export async function editarDispositivo(id, obj) { return (await API.put(`/api/dispositivos/${id}`, obj)).data }
export async function excluirDispositivo(id) { return API.delete(`/api/dispositivos/${id}`) }

// Mantemos o nome da funcao para o dashboard anterior continuar funcionando,
// mas a tabela/endpoint do banco passa a ser OCORRENCIAS.
function paraDashboard(ocorrencia) {
  return {
    ...ocorrencia,
    tipo: ocorrencia.tipoSom,
    registradoEm: ocorrencia.dataHora,
    simulado: true, // Todos os registros desta demonstracao sao manuais.
  }
}
export async function listarEventos() {
  const registros = (await API.get('/api/ocorrencias')).data
  return registros.map(paraDashboard)
}
export async function criarEvento(obj) {
  return (await API.post('/api/ocorrencias', {
    dispositivoId: obj.dispositivoId,
    tipoSom: obj.tipoSom,
    decibeis: obj.decibeis,
    localizacao: obj.localizacao,
  })).data
}
export async function excluirEvento(id) { return API.delete(`/api/ocorrencias/${id}`) }

// Medicoes de amplitude ADC do microfone MAX9814: nao sao dB.
export async function listarLeituras() { return (await API.get('/api/leituras')).data }

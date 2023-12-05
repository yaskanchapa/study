<script>
import _ from 'lodash' // eslint-disable-line

export function getAllRowsData(gridApi) {
  let rowData = [] // eslint-disable-line
  gridApi.forEachNode(node => rowData.push(node.data))
  return rowData
}

export function getSingleRowData(gridApi, id) {
  const rowNode = gridApi.getRowNode(id)
  return rowNode.data
}

export function getSingleNode(gridApi, id) {
  return gridApi.getRowNode(id)
}

export function setSingleCellValue(gridApi, id, field, value, isRefresh = true) {
  const rowNode = gridApi.getRowNode(id)
  const newData = rowNode.data
  newData[field] = value
  rowNode.setData(newData)

  // Component(Button, Swithなど)をcellRendererに指定した場合はRefresh(再描画)が必要。
  if (isRefresh) {
    gridApi.refreshCells({ force: true, rowNodes: [rowNode] })
  }
}

export function setSingleRowData(gridApi, id, data, isRefresh = true) {
  const rowNode = gridApi.getRowNode(id)
  rowNode.setData(data)

  // Component(Button, Swithなど)をcellRendererに指定した場合はRefresh(再描画)が必要。
  if (isRefresh) {
    gridApi.refreshCells({ force: true, rowNodes: [rowNode] })
  }
}

export function setSingleCellData(gridApi, id, field, data, isRefresh = true) {
  const rowNode = gridApi.getRowNode(id)
  const newData = rowNode.data;
  newData[field] = data
  rowNode.setData(newData)

  // Component(Button, Swithなど)をcellRendererに指定した場合はRefresh(再描画)が必要。
  if (isRefresh) {
    gridApi.refreshCells({ force: true, rowNodes: [rowNode] })
  }
}

export function setAllRowData(gridApi, allData, isRefresh = true) {
  const allRowNode = []
  gridApi.forEachNode(function(rowNode, index) {
    const data = allData[index]
    rowNode.setData(data)
    allRowNode.push(rowNode)
  })

  // Component(Button, Swithなど)をcellRendererに指定した場合はRefresh(再描画)が必要。
  if (isRefresh) {
    gridApi.refreshCells({ force: true, rowNodes: allRowNode })
  }
}

export function getSelectdRowsData(gridApi) {
  return gridApi.getSelectedRows()
}

export function getSelectedNodes(gridApi) {
  return gridApi.getSelectedNodes()
}

export default { getAllRowsData, getSingleRowData, getSingleNode, setSingleRowData, getSelectdRowsData, getSelectedNodes }
</script>

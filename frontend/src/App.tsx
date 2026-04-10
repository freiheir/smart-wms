import { useEffect, useState } from 'react'
import axios from 'axios'

interface Item {
  id: number;
  itemCode: string;
  itemName: string;
  description: string;
  price: number;
  stockQuantity: number;
}

function App() {
  const [items, setItems] = useState<Item[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const fetchItems = async () => {
      try {
        const response = await axios.get('/api/items')
        setItems(response.data)
        setLoading(false)
      } catch (err) {
        setError('Failed to fetch items.')
        setLoading(false)
        console.error(err)
      }
    }
    fetchItems()
  }, [])

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <h1>Smart WMS/ERP Product Showcase</h1>
      {loading && <p>Loading products...</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '20px' }}>
        {items.map(item => (
          <div key={item.id} style={{ border: '1px solid #ddd', padding: '15px', borderRadius: '8px' }}>
            <h3>{item.itemName}</h3>
            <p>{item.description}</p>
            <p><strong>Price:</strong> {item.price.toLocaleString()} KRW</p>
            <p><strong>Stock:</strong> {item.stockQuantity} available</p>
          </div>
        ))}
      </div>
    </div>
  )
}

export default App

import React, { useState, useEffect, useRef } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useLocation, useNavigate } from 'react-router-dom';
import { Home, LogIn, LogOut, Package, Search, Menu, ChevronRight, Bell, X, AlertCircle, CheckCircle2, RefreshCw } from 'lucide-react';
import { Html5Qrcode } from 'html5-qrcode';
import axios from 'axios';

// --- 타입 정의 ---
interface Item {
  id: number;
  itemCode: string;
  itemName: string;
  itemUnit: string;
  price: number;
  stockQuantity: number;
  barcode: string;
}

// --- 바코드 스캐너 컴포넌트 ---
const BarcodeScanner = ({ onScan, onClose }: { onScan: (decodedText: string) => void, onClose: () => void }) => {
  const scannerRef = useRef<Html5Qrcode | null>(null);
  const [isInitializing, setIsInitializing] = useState(true);
  const [cameraDevices, setCameraDevices] = useState<any[]>([]);
  const [currentCameraIndex, setCurrentCameraIndex] = useState(-1);

  const startScanner = async (index: number, devices: any[]) => {
    if (!devices[index]) return;
    try {
      setIsInitializing(true);
      if (!scannerRef.current) scannerRef.current = new Html5Qrcode("reader");
      if (scannerRef.current.isScanning) await scannerRef.current.stop();

      await scannerRef.current.start(
        devices[index].id,
        { 
          fps: 20, 
          qrbox: { width: 280, height: 180 },
          videoConstraints: { focusMode: "continuous" }
        },
        (decodedText) => {
          onScan(decodedText);
          scannerRef.current?.stop().catch(console.error);
        },
        () => { }
      );
      setIsInitializing(false);
    } catch (err) {
      setIsInitializing(false);
    }
  };

  useEffect(() => {
    const init = async () => {
      try {
        const devices = await Html5Qrcode.getCameras();
        if (devices && devices.length > 0) {
          setCameraDevices(devices);
          // 후면 카메라(마지막 장치)로 기본 설정
          const lastIdx = devices.length - 1;
          setCurrentCameraIndex(lastIdx);
          setTimeout(() => startScanner(lastIdx, devices), 500);
        } else {
          onClose();
        }
      } catch (err) {
        onClose();
      }
    };
    init();
    return () => {
      if (scannerRef.current?.isScanning) {
        scannerRef.current.stop().catch(console.error);
      }
    };
  }, []);

  const handleToggle = () => {
    if (cameraDevices.length < 2) return;
    const nextIdx = (currentCameraIndex + 1) % cameraDevices.length;
    setCurrentCameraIndex(nextIdx);
    startScanner(nextIdx, cameraDevices);
  };

  return (
    <div className="fixed inset-0 bg-black z-50 flex flex-col">
      <div className="p-4 flex justify-between items-center text-white bg-gray-900/90 z-10">
        <button onClick={onClose} className="p-2 bg-gray-800 rounded-full active:scale-90"><X size={20} /></button>
        <h3 className="font-bold text-sm tracking-tight">바코드 스캔</h3>
        <button onClick={handleToggle} className="p-2 bg-blue-600 rounded-full active:scale-90 transition-transform shadow-lg">
          <RefreshCw size={18} />
        </button>
      </div>
      <div className="flex-1 bg-black relative flex items-center justify-center overflow-hidden">
        {isInitializing && (
          <div className="absolute inset-0 flex flex-col items-center justify-center z-20 bg-black text-white">
            <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin mb-4"></div>
            <p className="text-xs font-medium">카메라를 연결하고 있습니다...</p>
          </div>
        )}
        <div id="reader" style={{ width: '100%', height: '100%' }}></div>
      </div>
      <div className="p-8 text-center text-gray-500 text-xs bg-gray-900 z-10">
        상자나 전표의 바코드를 사각형 안에 비춰주세요
      </div>
    </div>
  );
};

// 🏠 대시보드
const Dashboard = () => {
  const navigate = useNavigate();
  const mainMenu = [
    { id: 'inbound', label: '입고 관리', icon: <LogIn size={40} />, color: 'text-blue-600', bg: 'bg-blue-50', path: '/inbound' },
    { id: 'outbound', label: '출고 관리', icon: <LogOut size={40} />, color: 'text-green-600', bg: 'bg-green-50', path: '/outbound' },
    { id: 'stock', label: '재고 관리', icon: <Package size={40} />, color: 'text-orange-600', bg: 'bg-orange-50', path: '/stock' },
    { id: 'item', label: '상품 조회', icon: <Search size={40} />, color: 'text-purple-600', bg: 'bg-purple-50', path: '/item' },
  ];

  return (
    <div className="p-5 space-y-6 bg-gray-50 min-h-screen">
      <div className="flex items-center justify-between bg-white p-4 rounded-xl shadow-sm">
        <div>
          <p className="text-sm text-gray-500">반갑습니다!</p>
          <p className="font-bold text-lg">현장 작업자 01님</p>
        </div>
        <div className="bg-blue-600 text-white p-2 rounded-full"><Bell size={20} /></div>
      </div>
      <div className="grid grid-cols-2 gap-4">
        {mainMenu.map((item) => (
          <button key={item.id} onClick={() => navigate(item.path)} className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex flex-col items-center justify-center gap-3 transition-transform active:scale-95 h-40">
            <div className={`${item.bg} ${item.color} p-4 rounded-2xl`}>{item.icon}</div>
            <span className="font-bold text-gray-800">{item.label}</span>
          </button>
        ))}
      </div>
    </div>
  );
};

// 📥 입고 관리
const Inbound = () => {
  const [isScanning, setIsScanning] = useState(false);
  const [scannedCode, setScannedCode] = useState<string | null>(null);
  const [item, setItem] = useState<Item | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchItemInfo = async (code: string) => {
    setLoading(true);
    setError(null);
    setItem(null);
    try {
      const response = await axios.get(`/api/items/search?code=${code}`);
      setItem(response.data);
    } catch (err: any) {
      setError("등록되지 않은 상품입니다.");
    } finally {
      setLoading(false);
    }
  };

  const handleScan = (code: string) => {
    setScannedCode(code);
    setIsScanning(false);
    fetchItemInfo(code);
  };

  return (
    <div className="p-5 space-y-4">
      {isScanning && <BarcodeScanner onScan={handleScan} onClose={() => setIsScanning(false)} />}
      
      <div className="bg-white p-8 rounded-3xl border-2 border-dashed border-blue-200 flex flex-col items-center gap-4 min-h-[200px] justify-center text-center shadow-inner">
        {loading ? (
          <div className="animate-pulse text-blue-500 font-bold">상품 정보를 조회하는 중...</div>
        ) : scannedCode ? (
          item ? (
            <div className="w-full">
              <div className="bg-green-50 text-green-600 p-3 rounded-full inline-block mb-2"><CheckCircle2 size={32} /></div>
              <p className="text-xs text-gray-400 font-bold tracking-widest uppercase">등록 상품</p>
              <h3 className="text-xl font-black text-gray-900 mt-1">{item.itemName}</h3>
              <p className="text-sm text-gray-500 mt-1">{item.itemCode} | {item.itemUnit}</p>
            </div>
          ) : (
            <div className="w-full">
              <div className="bg-red-50 text-red-500 p-3 rounded-full inline-block mb-2"><AlertCircle size={32} /></div>
              <p className="text-xs text-red-400 font-bold tracking-widest uppercase">조회 실패</p>
              <p className="text-lg font-bold text-red-600 mt-1">{error}</p>
              <p className="text-xs text-gray-400 mt-1 break-all">코드: {scannedCode}</p>
            </div>
          )
        ) : (
          <>
            <div className="bg-blue-50 p-4 rounded-full text-blue-500 mb-2">
              <Search size={40} />
            </div>
            <p className="text-gray-500 font-medium text-sm">카메라를 사용하여<br/>바코드를 스캔하세요</p>
          </>
        )}
      </div>

      <button onClick={() => setIsScanning(true)} className="w-full bg-blue-600 text-white py-4 rounded-2xl font-bold text-lg shadow-lg active:bg-blue-700 flex items-center justify-center gap-2 transition-all">
        <Search size={24} /> 바코드 스캔 시작
      </button>

      {item && (
        <div className="bg-white p-5 rounded-3xl shadow-xl border border-blue-50 space-y-4 animate-in slide-in-from-bottom-4 duration-300">
          <div className="flex justify-between items-center border-b pb-3">
            <span className="text-xs font-black text-gray-400 uppercase">입고 수량 입력</span>
            <span className="text-xs bg-blue-100 text-blue-600 px-2 py-1 rounded-full font-bold">현재고: {item.stockQuantity}</span>
          </div>
          <div className="flex items-center gap-4">
            <input type="number" defaultValue="1" className="flex-1 text-center text-3xl font-black p-3 border-2 border-gray-100 rounded-2xl bg-gray-50 focus:border-blue-500 outline-none transition-colors" />
            <span className="text-xl font-bold text-gray-600">{item.itemUnit}</span>
          </div>
          <button className="w-full bg-blue-600 text-white py-4 rounded-2xl font-bold text-lg shadow-blue-200 shadow-lg active:scale-95 transition-transform">
            입고 완료 처리
          </button>
        </div>
      )}

      {error && (
        <button className="w-full bg-gray-900 text-white py-4 rounded-2xl font-bold active:scale-95 transition-transform">
          현장에서 상품 신규 등록
        </button>
      )}
    </div>
  );
};

// --- 레이아웃 ---
const AppLayout = ({ children }: { children: React.ReactNode }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const isHome = location.pathname === '/';
  const getTitle = () => {
    switch(location.pathname) {
      case '/inbound': return '입고 처리';
      case '/outbound': return '출고 관리';
      case '/stock': return '재고 관리';
      case '/item': return '상품 조회';
      default: return 'Smart WMS';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 font-sans text-gray-900 pb-10">
      <header className="bg-white p-4 flex items-center justify-between border-b sticky top-0 z-20 shadow-sm">
        <div className="flex items-center gap-3">
          {!isHome && (
            <button onClick={() => navigate('/')} className="p-2 hover:bg-gray-100 rounded-full text-gray-600 transition-colors"><Home size={22} /></button>
          )}
          <h1 className="text-lg font-bold tracking-tight">{getTitle()}</h1>
        </div>
        <div className="flex items-center gap-2"><button className="p-2 text-gray-400"><Menu size={22} /></button></div>
      </header>
      <main className="max-w-md mx-auto">{children}</main>
    </div>
  );
};

const App: React.FC = () => {
  return (
    <Router>
      <AppLayout>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/inbound" element={<Inbound />} />
          <Route path="/outbound" element={<div className="p-10 text-center text-gray-400">준비 중</div>} />
          <Route path="/stock" element={<div className="p-10 text-center text-gray-400">준비 중</div>} />
          <Route path="/item" element={<div className="p-10 text-center text-gray-400">준비 중</div>} />
        </Routes>
      </AppLayout>
    </Router>
  );
};

export default App;
